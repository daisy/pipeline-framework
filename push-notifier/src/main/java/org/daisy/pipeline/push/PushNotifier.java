package org.daisy.pipeline.push;

import org.daisy.common.messaging.Message;
import org.daisy.pipeline.event.EventBusProvider;
import org.daisy.pipeline.job.Job;
import org.osgi.framework.BundleContext;

import com.google.common.eventbus.Subscribe;

// notify clients whenever there are new messages or a change in status
// TODO: be sure to only do this N times per second
public class PushNotifier {
	CallbackRegistry callbackRegistry;
	EventBusProvider eventBusProvider;

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

	@Subscribe
	public synchronized void handleMessage(Message msg) {
		System.out.print("***********" + msg.getText());
	}

	public static void notifyNewMessage(Message msg) {
		// TODO
	}

	public static void notifyNewStatus(Job job) {
		// TODO
	}


}