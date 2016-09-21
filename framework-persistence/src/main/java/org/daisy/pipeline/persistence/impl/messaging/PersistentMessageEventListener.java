package org.daisy.pipeline.persistence.impl.messaging;

import javax.persistence.EntityManagerFactory;

import org.daisy.common.messaging.Message;
import org.daisy.pipeline.event.EventBusProvider;
import org.daisy.pipeline.persistence.impl.Database;

import com.google.common.eventbus.Subscribe;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * This class receives message events and stores them in memory and gives access
 * to them via the accessor interface. The class that is interested in
 * processing a memoryMessage
 * 
 */
@Component(
	name = "event-bus-listener",
	immediate = true
)
public class PersistentMessageEventListener {

	EventBusProvider eventBusProvider;
	Database datbase;

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

	@Reference(
		name = "entity-manager-factory",
		unbind = "-",
		service = EntityManagerFactory.class,
		target = "(osgi.unit.name=pipeline-pu)",
		cardinality = ReferenceCardinality.MANDATORY,
		policy = ReferencePolicy.STATIC
	)
	public synchronized void setEntityManagerFactory(EntityManagerFactory emf) {
		this.datbase = new Database(emf);
	}

	@Subscribe
	public synchronized void handleMessage(Message msg) {
		if (datbase != null) {
			datbase.addObject(new PersistentMessage(msg));
		}
	}

}
