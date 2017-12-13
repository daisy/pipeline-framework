package org.daisy.pipeline.nonpersistent.impl.messaging;

import java.util.function.BiConsumer;

import org.daisy.common.messaging.Message;
import org.daisy.pipeline.event.EventBusProvider;
import org.daisy.pipeline.event.ProgressMessage;
import org.daisy.pipeline.event.ProgressMessageUpdate;

import com.google.common.eventbus.Subscribe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class receives message events and stores them in memory.  
 */
public class VolatileMessageEventListener {

	private static Logger logger = LoggerFactory.getLogger(VolatileMessageEventListener.class);

	private EventBusProvider eventBusProvider;

	public void setEventBusProvider(EventBusProvider eventBusProvider) {
		this.eventBusProvider = eventBusProvider;
		this.eventBusProvider.get().register(this);
	}

	@Subscribe
	public synchronized void handleMessage(ProgressMessage msg) {
		logger.trace("storing message: [job: " + msg.getJobId() + ", msg: " + msg.getSequence() + "]");
		VolatileMessageStorage storage = VolatileMessageStorage.getInstance();
		synchronized (storage) {
			if (storage.add(msg))
				for (BiConsumer<String,Integer> c : storage.onNewMessages)
					c.accept(msg.getJobId(), msg.getSequence());
		}
	}

	@Subscribe
	public void handleMessageUpdate(ProgressMessageUpdate update) {
		Message msg = update.getMessage();
		logger.trace("updating message: [job: " + msg.getJobId() + ", msg: " + msg.getSequence()
		             + ", event: " + update.getSequence() + "]");
		VolatileMessageStorage storage = VolatileMessageStorage.getInstance();
		synchronized (storage) {
			for (BiConsumer<String,Integer> c : storage.onNewMessages)
				c.accept(msg.getJobId(), update.getSequence());
		}
	}
}
