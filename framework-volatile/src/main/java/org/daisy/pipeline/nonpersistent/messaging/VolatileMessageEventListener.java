package org.daisy.pipeline.nonpersistent.messaging;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.daisy.common.messaging.Message;
import org.daisy.common.messaging.Message.Level;
import org.daisy.common.messaging.MessageAccessor;
import org.daisy.pipeline.event.EventBusProvider;

import com.google.common.eventbus.Subscribe;

/**
 * This class receives message events and stores them in memory and gives access
 * to them via the accessor interface. The class that is interested in
 * processing a memoryMessage
 *
 */
public class VolatileMessageEventListener {

	private EventBusProvider eventBusProvider;


	public void setEventBusProvider(EventBusProvider eventBusProvider) {
		this.eventBusProvider = eventBusProvider;
		this.eventBusProvider.get().register(this);
	}

	@Subscribe
	public synchronized void handleMessage(Message msg) {
		VolatileMessageStorage.getInstance().add(msg);
	}

}
