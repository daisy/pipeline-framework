package org.daisy.pipeline.push.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.daisy.common.messaging.Message;
import org.daisy.common.messaging.MessageAccessor;
import org.daisy.pipeline.clients.ClientStorage;
import org.daisy.pipeline.event.EventBusProvider;
import org.daisy.pipeline.event.ProgressMessage;
import org.daisy.pipeline.event.ProgressMessageUpdate;
import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.Job.Status;
import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.job.JobManager;
import org.daisy.pipeline.job.JobManagerFactory;
import org.daisy.pipeline.job.JobUUIDGenerator;
import org.daisy.pipeline.job.StatusMessage;
import org.daisy.pipeline.webserviceutils.callback.Callback;
import org.daisy.pipeline.webserviceutils.callback.Callback.CallbackType;
import org.daisy.pipeline.webserviceutils.callback.CallbackRegistry;
import org.daisy.pipeline.webserviceutils.storage.WebserviceStorage;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

// notify clients whenever there are new messages or a change in status
// this class could evolve into a general notification utility
// e.g. it could also trigger email notifications
// TODO: be sure to only do this N times per second
public class PushNotifier {


        private CallbackRegistry callbackRegistry;
        private EventBusProvider eventBusProvider;
        private JobManagerFactory jobManagerFactory;
        private ClientStorage clientStorage;
        private JobManager jobManager;

        /** The logger. */
        private static Logger logger = LoggerFactory.getLogger(PushNotifier.class); 
        // for now: push notifications every second. TODO: support different frequencies.
        final int PUSH_INTERVAL = 1000;

        // track the starting point in the message sequence for every timed push
        private Map<JobId,Integer> jobsWithNewMessages = Collections.synchronizedMap(new HashMap<JobId,Integer>());
        private List<StatusHolder> statusList= Collections.synchronizedList(new LinkedList<StatusHolder>());

        Timer timer = null;

        public PushNotifier() {
        }

        public void init(BundleContext context) {
                logger = LoggerFactory.getLogger(Poster.class.getName());
                logger.debug("Activating push notifier");
                jobManager = jobManagerFactory.createFor(clientStorage.defaultClient());
                this.startTimer();

        }

        public void close() {
                cancelTimer();
        }

        public synchronized void startTimer() {
                timer = new Timer();
                timer.schedule(new NotifyTask(), 0, PUSH_INTERVAL);
        }
        public synchronized void cancelTimer() {
                if (timer != null) {
                        timer.cancel();
                        timer = null;
                }
        }

        public void setEventBusProvider(EventBusProvider eventBusProvider) {
                this.eventBusProvider = eventBusProvider;
                this.eventBusProvider.get().register(this);
        }

        /**
         * @param clientStorage the clientStorage to set
         */
        public void setWebserviceStorage(WebserviceStorage storage) {
                this.clientStorage = storage.getClientStorage();
                
        }

        public void setCallbackRegistry(CallbackRegistry callbackRegistry) {
                this.callbackRegistry = callbackRegistry;
        }

        public void setJobManagerFactory(JobManagerFactory jobManagerFactory) {
                this.jobManagerFactory = jobManagerFactory;
        }

        @Subscribe
        public void handleMessage(ProgressMessage msg) {
                logger.trace("handling message: [job: " + msg.getJobId() + ", msg: " + msg.getSequence() + "]");
                handleMessage(msg.getJobId(), msg.getSequence());
        }

        @Subscribe
        public void handleMessageUpdate(ProgressMessageUpdate event) {
                logger.trace("handling message update: [job: " + event.getMessage().getJobId()
                             + ", msg: " + event.getMessage().getSequence()
                             + ", event: " + event.getSequence() + "]");
                handleMessage(event.getMessage().getJobId(), event.getSequence());
        }

        private void handleMessage(String jobId, int sequence) {
                JobUUIDGenerator gen = new JobUUIDGenerator();
                JobId id = gen.generateIdFromString(jobId);
                synchronized (jobsWithNewMessages) {
                        if (!jobsWithNewMessages.containsKey(id)) {
                                jobsWithNewMessages.put(id, sequence);
                        }
                }
        }

        @Subscribe
        public void handleStatus(StatusMessage message) {
                logger.debug(String.format("Status changed %s->%s",message.getJobId(),message.getStatus()));
                StatusHolder holder= new StatusHolder();
                holder.status=message.getStatus();
                Optional<Job> job=jobManager.getJob(message.getJobId());
                if(job.isPresent()){
                        holder.job=job.get();
                }
                statusList.add(holder);

        }


        private class NotifyTask extends TimerTask {
                public NotifyTask() {
                        super();
                }

                @Override
                public synchronized void run() {
                        postMessages();
                        postStatus();
                }
                private void postStatus() {
                        //logger.debug("Posting messages");
                        List<StatusHolder> toPost=Lists.newLinkedList();
                        synchronized(PushNotifier.this.statusList){
                                toPost.addAll(PushNotifier.this.statusList);    
                                PushNotifier.this.statusList.clear();
                        }
                        for (StatusHolder holder: toPost) {
                                logger.debug("Posting status '" + holder.status + "' for job " + holder.job.getId());
                                Job job = holder.job;

                                for (Callback callback :callbackRegistry.getCallbacks(job.getContext().getId())) {
                                        if (callback.getType() == CallbackType.STATUS) {
                                                Poster.postStatusUpdate(job, holder.status, callback);
                                        }
                                }
                        }

                }
                private synchronized void postMessages() {
                        Map<JobId,Integer> jobsWithNewMessages; {
                                synchronized (PushNotifier.this.jobsWithNewMessages) {
                                        jobsWithNewMessages = new HashMap<JobId,Integer>(PushNotifier.this.jobsWithNewMessages);
                                        PushNotifier.this.jobsWithNewMessages.clear();
                                }
                        }
                        for (JobId jobId : jobsWithNewMessages.keySet()) {
                                Optional<Job> job = jobManager.getJob(jobId);
                                if(!job.isPresent()){
                                        continue;
                                }
                                MessageAccessor accessor = job.get().getContext().getMonitor().getMessageAccessor();
                                if (accessor != null) {
                                        int seq = jobsWithNewMessages.get(jobId);
                                        BigDecimal progress;
                                        List<Message> messages;
                                        {
                                                progress = accessor.getProgress();
                                                logger.debug("Posting messages starting from " + seq + " for job " + job.get().getId());
                                                messages = accessor.createFilter().greaterThan(seq - 1).getMessages();
                                        }
                                        for (Callback callback : callbackRegistry.getCallbacks(jobId)) {
                                                if (callback.getType() == CallbackType.MESSAGES) {
                                                        Poster.postMessages(job.get(), messages, progress, callback);
                                                }
                                        }
                                }
                        }

                        // no need to keep the timer going if there are no more messages
                        // however, this doesn't really work. TODO fix it.
                        /*if (messages.isEmpty()) {
                          System.out.println("Cancelling timer");
                          cancelTimer();
                          }*/
                }
        }
        
        /*
         * In order to not lose the reference 
         * to the job if it's been deleted
         */
        private class StatusHolder{
                Status status;
                Job job;
        }

}
