package org.daisy.pipeline.event;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.daisy.common.messaging.Message;
import org.daisy.common.messaging.MessageAccessor;
import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.job.JobMonitor;
import org.daisy.pipeline.job.JobMonitorFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.eventbus.EventBus;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
	name = "job-monitor",
	service = { JobMonitorFactory.class }
)
public class JobMonitorFactoryImpl implements JobMonitorFactory {

	private static Logger logger = LoggerFactory.getLogger(JobMonitorFactoryImpl.class);

	@Override
	public JobMonitor newJobMonitor(JobId id) {
		return new JobMonitorImpl(id.toString());
	}

	@Override
	public JobMonitor newJobMonitor(JobId id, MessageAccessor messageAccessor) {
		return new JobMonitorImpl(id.toString(), messageAccessor);
	}

	private EventBus eventBus;

	private MessageStorage messageStorage;
	
	@Reference(
		name = "message-storage",
		unbind = "-",
		service = MessageStorage.class,
		cardinality = ReferenceCardinality.MANDATORY,
		policy = ReferencePolicy.STATIC
	)
	public void setMessageStorage(MessageStorage storage) {
		this.messageStorage = storage;
	}

	@Reference(
		name = "event-bus-provider",
		unbind = "-",
		service = EventBusProvider.class,
		cardinality = ReferenceCardinality.MANDATORY,
		policy = ReferencePolicy.STATIC
	)
	public void setEventBus(EventBusProvider provider) {
		eventBus = provider.get();
	}

	// We need to cache the accessors because we wouldn't otherwise be able to create a new job
	// monitor while a job is already running. Another reason for the caching is that the message
	// storage is not guaranteed to be lossless. Notably the database can currently not store the
	// full message tree, so the messages are flattened first.
	private final Map<String,MessageAccessor> liveAccessors;

	public JobMonitorFactoryImpl() {
		// use this property to configure how long messages are cached before storing them in a MessageStorage (volatile of persistent)
		// use only for testing!
		// to configure how long messages are cached in the volatile storage, use org.daisy.pipeline.messaging.cache
		int timeout = Integer.valueOf(System.getProperty("org.daisy.pipeline.messaging.cache.buffer", "60"));
		liveAccessors = CacheBuilder.newBuilder()
			.expireAfterAccess(timeout, TimeUnit.SECONDS)
			.removalListener(
				notification -> {
					MessageAccessor a = (MessageAccessor)notification.getValue();
					// store buffered messages to memory or database
					logger.trace("Persisting messages to " + JobMonitorFactoryImpl.this.messageStorage);
					for (Message m : a.getAll())
						JobMonitorFactoryImpl.this.messageStorage.add(m); })
			.<String,MessageAccessor>build()
			.asMap();
	}

	private final class JobMonitorImpl implements JobMonitor {

		private final MessageAccessor accessor;

		private JobMonitorImpl(String id) {
			// If a accessor is cached, return it.
			if (liveAccessors.containsKey(id))
				this.accessor = liveAccessors.get(id);
			// Otherwise load the messages from storage (assumes the job has finished).
			else {
				liveAccessors.remove(id); // this is needed to trigger removal listener (why?)
				this.accessor = new MessageAccessorFromStorage(id, messageStorage);
			}
		}

		private JobMonitorImpl(String id, MessageAccessor accessor) {
			if (liveAccessors.containsKey(id))
				throw new IllegalArgumentException(); // a message accessor was already registered for this job
			else {
				liveAccessors.remove(id); // this is needed to trigger removal listener (why?)
				this.accessor = accessor;
				liveAccessors.put(id, this.accessor);
				logger.trace("Registered MessageAccessor for job " + id);
				// Keep the accessor in the cache for the time job is running. The accessor will
				// be evicted 60 seconds after the last message has arrived.
				this.accessor.listen(i -> liveAccessors.get(id));
			}
		}

		@Override
		public MessageAccessor getMessageAccessor() {
			return accessor;
		}

		@Override
		public EventBus getEventBus() {
			return eventBus;
		}
	}
}
