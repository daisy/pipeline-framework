package org.daisy.pipeline.push;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.daisy.common.messaging.Message;
import org.daisy.pipeline.event.EventBusProvider;
import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.job.JobManager;
import org.daisy.pipeline.job.JobUUIDGenerator;
import org.daisy.pipeline.job.StatusMessage;
import org.daisy.pipeline.webserviceutils.callback.Callback;
import org.daisy.pipeline.webserviceutils.callback.Callback.CallbackType;
import org.daisy.pipeline.webserviceutils.callback.CallbackRegistry;
import org.osgi.framework.BundleContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;

// notify clients whenever there are new messages or a change in status
// this class could evolve into a general notification utility
// e.g. it could also trigger email notifications
// TODO: be sure to only do this N times per second
public class PushNotifier {


	private CallbackRegistry callbackRegistry;
	private EventBusProvider eventBusProvider;
	private JobManager jobManager;
	/** The logger. */
	private Logger logger;// = LoggerFactory.getLogger(Poster.class.getName());
               
	// for now: push notifications every second. TODO: support different frequencies.
	final int PUSH_INTERVAL = 1000;

	// track the starting point in the message sequence for every timed push
	// TODO something similar for status
	MessageList messages;

	Timer timer = null;
	boolean started = false;

	public PushNotifier() {
	}

	public void init(BundleContext context) {
		messages = new MessageList();
		logger = LoggerFactory.getLogger(Poster.class.getName());

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

	public void setCallbackRegistry(CallbackRegistry callbackRegistry) {
		this.callbackRegistry = callbackRegistry;
	}

	public void setJobManager(JobManager jobManager) {
		this.jobManager = jobManager;
	}

	@Subscribe
	public synchronized void handleMessage(Message msg) {
		if (started == false) {
			started = true;
			startTimer();
		}
		JobUUIDGenerator gen = new JobUUIDGenerator();
		JobId jobId = gen.generateIdFromString(msg.getJobId());
		messages.addMessage(jobId, msg);
	}

	@Subscribe
	public synchronized void handleStatus(StatusMessage message) {
		logger.debug(String.format("Status changed %s->%s",message.getJobId(),message.getStatus()));
	}


	class NotifyTask extends TimerTask {
		public NotifyTask() {
			super();
		}

		@Override
		public synchronized void run() {
			//System.out.println("Timer running");
			postMessages();
		}
		private synchronized void postMessages() {
			synchronized(PushNotifier.this){
			//logger.debug("Posting messages");
			//logger.debug("Jobs cnt: "+messages.getJobs().size());
				for (JobId jobId : messages.getJobs()) {
					Job job = jobManager.getJob(jobId);
					for (Callback callback : callbackRegistry.getCallbacks(jobId)) {
						if (callback.getType() == CallbackType.MESSAGES) {
							Poster.postMessage(job, new LinkedList<Message>(messages.getMessages(jobId)), callback);
						}
					}
					//I don't mind noone listening for the messages they will be discarded anyway...
					messages.removeJob(jobId);
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

	class MessageList {
		HashMap<JobId, List<Message>> messages;

		public MessageList() {
			messages = new HashMap<JobId, List<Message>>();
		}
		public synchronized List<Message> getMessages(JobId jobId) {
			return messages.get(jobId);
		}
		public synchronized MessageList copy(){
			MessageList copy=new MessageList();	
			for (Map.Entry<JobId,List<Message>> entry:this.messages.entrySet()){
				copy.messages.put(entry.getKey(),new LinkedList<Message>(entry.getValue()));	
			}
			return copy;
		}

		public synchronized void addMessage(JobId jobId, Message msg) {
			List<Message> list;
			if (containsJob(jobId)) {
				list = messages.get(jobId);
			}
			else {
				list = new ArrayList<Message>();
				messages.put(jobId, list);
			}
			list.add(msg);
		}
		public synchronized Set<JobId> getJobs() {
			return messages.keySet();
		}

		public synchronized void removeJob(JobId jobId) {
			messages.remove(jobId);
		}

		public synchronized boolean containsJob(JobId jobId) {
			return messages.containsKey(jobId);
		}

		public synchronized boolean isEmpty() {
			return messages.isEmpty();
		}

		// for debugging
		public synchronized void printList(JobId jobId) {
			for (Message msg : messages.get(jobId)) {
				System.out.println("#" + msg.getSequence() + ", job #" + msg.getJobId());
			}
		}
	}

}
