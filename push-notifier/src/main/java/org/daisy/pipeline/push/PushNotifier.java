package org.daisy.pipeline.push;

import java.util.HashMap;
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

import com.google.common.eventbus.Subscribe;

// notify clients whenever there are new messages or a change in status
// this class could evolve into a general notification utility
// e.g. it could also trigger email notifications
// TODO: be sure to only do this N times per second
public class PushNotifier {
	private CallbackRegistry callbackRegistry;
	private EventBusProvider eventBusProvider;
	private JobManager jobManager;

	// for now: push notifications every second. TODO: support different frequencies.
	final int PUSH_INTERVAL = 1000;

	// track the starting point in the message sequence for every timed push
	// TODO something similar for status
	MessageSeq messages;

	Timer timer;

	public PushNotifier() {
	}

	public void init(BundleContext context) {
		messages = new MessageSeq();
		timer = new Timer();
	}

	public void close() {
		timer.cancel();
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
		// if this is our first message, start the timer.
		if (messages.isEmpty()) {
			timer.schedule(new MessageTask(), 0, PUSH_INTERVAL);
		}

		JobUUIDGenerator gen = new JobUUIDGenerator();
		JobId jobId = gen.generateIdFromString(msg.getJobId());
		if (!messages.containsJob(jobId)) {
			// track the starting point in the messages sequence
			messages.setMessageSeq(jobId, msg.getSequence());
		}

	}

	//TODO @Subscribe
	public synchronized void handleStatus(Job job) {
		// TODO handle similarly to messages
	}

	class MessageSeq {
		HashMap<JobId, Integer> messages;

		public MessageSeq() {
			messages = new HashMap<JobId, Integer>();
		}
		public synchronized int getMessageSeq(JobId jobId) {
			return messages.get(jobId);
		}

		public synchronized void setMessageSeq(JobId jobId, int idx) {
			messages.put(jobId, idx);
		}

		public synchronized Iterable<JobId> getJobs() {
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
	}

	class MessageTask extends TimerTask {
        @Override
		public synchronized void run() {
        	System.out.println("Timer running");
            postMessages();
        }

        private void postMessages() {
    		for (JobId jobId : messages.getJobs()) {
    			Iterable<Callback> callbacks = callbackRegistry.getCallbacks(jobId);
    			Job job = jobManager.getJob(jobId);
    			for (Callback callback : callbacks) {
    				if (callback.getType() == CallbackType.MESSAGES) {
    					// send messages for the job starting with the given message sequence index
    					int idx = messages.getMessageSeq(jobId);
    					messages.removeJob(jobId);
    					Poster.postMessage(job, idx, callback);
    				}
    			}
    			if (messages.isEmpty()) {
    				// TODO no need to keep the timer going if there are no messages
    				// but this doesn't work
    				// timer.cancel();
    			}
    		}

    	}

    }

}