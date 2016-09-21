package org.daisy.pipeline.nonpersistent.impl.messaging;

import org.daisy.common.messaging.Message;
import org.daisy.pipeline.event.EventBusProvider;

import com.google.common.eventbus.Subscribe;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * This class receives message events and stores them in memory.  
 */
@Component(
	name = "volatile-event-bus-listener",
	immediate = true
)
public class VolatileMessageEventListener {

	private EventBusProvider eventBusProvider;


	@Reference(
		name = "event-bus-provider",
		unbind = "-",
		service = EventBusProvider.class,
		cardinality = ReferenceCardinality.MANDATORY,
		policy = ReferencePolicy.STATIC
	)
	public void setEventBusProvider(EventBusProvider eventBusProvider) {
		this.eventBusProvider = eventBusProvider;
		this.eventBusProvider.get().register(this);
	}

	@Subscribe
	public synchronized void handleMessage(Message msg) {
		VolatileMessageStorage.getInstance().add(msg);
	}

}
