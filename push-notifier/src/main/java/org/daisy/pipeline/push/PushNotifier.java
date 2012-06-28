package org.daisy.pipeline.push;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.daisy.common.messaging.Message;
import org.daisy.pipeline.event.EventBusProvider;
import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.job.JobManager;
import org.daisy.pipeline.job.JobUUIDGenerator;
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
	private static Logger logger = LoggerFactory.getLogger(Poster.class.getName());

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
		//System.out.println("Received msg #" + msg.getSequence() + " for job #" + msg.getJobId());
		if (started == false) {
			started = true;
			startTimer();
		}
		JobUUIDGenerator gen = new JobUUIDGenerator();
		JobId jobId = gen.generateIdFromString(msg.getJobId());
		// if there is no entry for this job OR if the entry contains a start value greater than this sequence value
		if (!messages.containsJob(jobId) || messages.getMessageRange(jobId).start > msg.getSequence()) {
			System.out.println("*******Start seq with msg #" + msg.getSequence() + " for job #" + msg.getJobId());
			messages.setMessageRangeStart(jobId, msg.getSequence());
		}
		else {
			System.out.println("*******End   seq with msg #" + msg.getSequence() + " for job #" + msg.getJobId());
			// every now and then, we get a message out of order, e.g. #30 before #29, so we need to be sure not to lose it.
			if (msg.getSequence() > messages.getMessageRange(jobId).end) {
				messages.setMessageRangeEnd(jobId, msg.getSequence());
			}

		}
	}

	//TODO @Subscribe
	public synchronized void handleStatus(Job job) {
		// TODO handle similarly to messages
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
        	// make a copy of the jobIds
        	// they will not remain static because we are removing them as we go
        	// TODO maybe there's a more java-ish way to do this? it feels a bit ugly.
        	List<JobId> jobIds = new ArrayList<JobId>();
        	for (JobId jobId : messages.getJobs()) {
        		jobIds.add(jobId);
        	}

    		for (JobId jobId : jobIds) {
    			Iterable<Callback> callbacks = callbackRegistry.getCallbacks(jobId);
    			Job job = jobManager.getJob(jobId);
    			for (Callback callback : callbacks) {
    				if (callback.getType() == CallbackType.MESSAGES) {
    					MessageRange range = messages.getMessageRange(jobId);
    					messages.removeJob(jobId);
    					System.out.println("*******Pushing from #" + range.start + " to #" + range.end + " for job " + jobId.toString());
    					Poster.postMessage(job, range.start, range.end, callback);
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

	class MessageRange {
		public int start;
		public int end;
	}

	class MessageList {
		HashMap<JobId, MessageRange> messages;

		public MessageList() {
			messages = new HashMap<JobId, MessageRange>();
		}
		public MessageRange getMessageRange(JobId jobId) {
			return messages.get(jobId);
		}

		public void setMessageRangeStart(JobId jobId, int idx) {
			MessageRange range = new MessageRange();
			range.start = idx;
			range.end = idx;
			messages.put(jobId, range);
		}
		public void setMessageRangeEnd(JobId jobId, int idx) {
			MessageRange range = messages.get(jobId);
			if (range == null) { // this shouldn't ever happen
				return;
			}
			range.end = idx;
		}

		public Iterable<JobId> getJobs() {
			return messages.keySet();
		}

		public void removeJob(JobId jobId) {
			messages.remove(jobId);
		}

		public boolean containsJob(JobId jobId) {
			return messages.containsKey(jobId);
		}

		public boolean isEmpty() {
			return messages.isEmpty();
		}
	}

}