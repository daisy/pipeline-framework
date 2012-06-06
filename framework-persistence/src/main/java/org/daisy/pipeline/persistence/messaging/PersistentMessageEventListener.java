package org.daisy.pipeline.persistence.messaging;

import org.daisy.common.messaging.Message;
import org.daisy.pipeline.event.EventBusProvider;
import org.daisy.pipeline.persistence.Database;

import com.google.common.eventbus.Subscribe;

/**
 * This class receives message events and stores them in memory and gives access
 * to them via the accessor interface. The class that is interested in
 * processing a memoryMessage
 * 
 */
public class PersistentMessageEventListener {

	EventBusProvider eventBusProvider;
	Database datbase;

	public void setEventBusProvider(EventBusProvider eventBusProvider) {
		this.eventBusProvider = eventBusProvider;
		this.eventBusProvider.get().register(this);
	}

	public synchronized void setDatabase(Database database) {
		this.datbase = database;
	}
	public synchronized void unsetDatabase() {
		this.datbase = null;
	}

	@Subscribe
	public synchronized void handleMessage(Message msg) {
		if (datbase != null) {
			datbase.addObject(new PersistentMessage(msg));
		}
	}

}