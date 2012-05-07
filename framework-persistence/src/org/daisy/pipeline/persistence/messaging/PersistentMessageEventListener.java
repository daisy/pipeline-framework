package org.daisy.pipeline.persistence.messaging;

import org.daisy.common.messaging.Message;
import org.daisy.pipeline.event.EventBusProvider;
import org.daisy.pipeline.persistence.BasicDatabaseManager;

import com.google.common.eventbus.Subscribe;


/**
 * This class receives message events and stores them in memory and gives access
 * to them via the accessor interface. The class that is interested in
 * processing a memoryMessage
 * 
 */
public class PersistentMessageEventListener{
	
	EventBusProvider eventBusProvider;
	
	public void setEventBusProvider(EventBusProvider eventBusProvider){
		this.eventBusProvider=eventBusProvider;
		this.eventBusProvider.get().register(this);
	}
	@Subscribe
	public void handleMessage(Message msg){
		new BasicDatabaseManager().addObject(new PersistentMessage(msg));
	}
	
}