package org.daisy.pipeline.event;

import java.util.concurrent.Executors;

import com.google.common.base.Supplier;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;

public class EventBusProvider implements Supplier<EventBus>{
	private final EventBus mEventBus=new AsyncEventBus(Executors.newCachedThreadPool());

	@Override
	public EventBus get() {
		return mEventBus;

	}

}
