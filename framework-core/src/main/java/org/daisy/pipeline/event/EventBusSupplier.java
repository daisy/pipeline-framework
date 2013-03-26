package org.daisy.pipeline.event;

import com.google.common.base.Supplier;
import com.google.common.eventbus.EventBus;

public class EventBusSupplier implements Supplier<EventBus>{
	private final EventBus mEventBus=new EventBus();//AsyncEventBus(Executors.newFixedThreadPool(10));

	@Override
	public EventBus get() {
		return mEventBus;

	}

}
