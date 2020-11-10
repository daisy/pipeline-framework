package org.daisy.pipeline.event;

import com.google.common.base.Supplier;
import com.google.common.eventbus.EventBus;

import org.daisy.common.messaging.MessageAppender;
import org.daisy.common.messaging.MessageBuilder;
import org.daisy.common.messaging.ProgressMessage;

import org.osgi.service.component.annotations.Component;

import org.slf4j.MDC;

@Component(
	name = "event-bus-provider",
	service = { EventBusProvider.class }
)
public class EventBusProvider implements Supplier<EventBus>, MessageAppender {

	private final EventBus mEventBus = new EventBus();

	@Override
	public EventBus get() {
		return mEventBus;
	}

	/**
	 * Post a ProgressMessage to the bus, and post a MessageUpdate event every
	 * time the object is updated.
	 */
	public MessageAppender append(MessageBuilder message) {
		// FIXME: depends on MDC manipulation of DefaultJobExecutionService
		String jobId = MDC.get("jobid");
		if (jobId != null)
			message = message.withOwnerId(jobId);
		message.onUpdated(e -> EventBusProvider.this.get().post(e));
		MessageAppender m = message.build();
		get().post((ProgressMessage)m);
		return m;
	}

	public void close() {
		// can not close because there is only one global event bus
		throw new UnsupportedOperationException();
	}
}
