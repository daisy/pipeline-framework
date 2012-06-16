package org.daisy.pipeline.push;

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
// TODO: be sure to only do this N times per second
public class PushNotifier {
	CallbackRegistry callbackRegistry;
	EventBusProvider eventBusProvider;
	JobManager jobManager;

	public PushNotifier() {
	}

	public void init(BundleContext context) {
	}

	public void close() {
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
		JobUUIDGenerator gen = new JobUUIDGenerator();
		JobId jobId = gen.generateIdFromString(msg.getJobId());
		Job job = jobManager.getJob(jobId);
		Iterable<Callback> callbacks = callbackRegistry.getCallbacks(job.getId());
		for (Callback callback : callbacks) {
			if (callback.getType() == CallbackType.MESSAGES) {
				Poster.postMessage(job, msg.getSequence(), callback);
			}
		}

	}

	//TODO @Subscribe
	public synchronized void handleStatus(Job job) {
		Iterable<Callback> callbacks = callbackRegistry.getCallbacks(job.getId());
		for (Callback callback : callbacks) {
			if (callback.getType() == CallbackType.STATUS) {
				Poster.postStatusUpdate(job, callback);
			}
		}
	}

}