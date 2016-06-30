package org.daisy.pipeline.event;

import com.google.common.base.Supplier;
import com.google.common.eventbus.EventBus;

import org.osgi.service.component.annotations.Component;

@Component(
	name = "event-bus-provider",
	service = { EventBusProvider.class }
)
public class EventBusProvider implements Supplier<EventBus>{
	private final EventBus mEventBus=new EventBus();//AsyncEventBus(Executors.newFixedThreadPool(10));

	@Override
	public EventBus get() {
		return mEventBus;

	}

}
