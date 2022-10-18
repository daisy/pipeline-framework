package org.daisy.pipeline.job;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.HashMap;
import java.util.Map;

import org.daisy.common.messaging.Message;
import org.daisy.common.messaging.MessageAccessor;
import org.daisy.common.properties.Properties;
import org.daisy.pipeline.event.MessageStorage;

import com.google.common.cache.CacheBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobMonitorFactory {

	/**
	 * Get the monitor for a job.
	 *
	 * If a "live" monitor (for monitoring the job while it runs) is present in memory, always
	 * returns it. Otherwise loads the messages from storage.
	 */
	public JobMonitor newJobMonitor(JobId id) {
		return new JobMonitorImpl(id);
	}

	/**
	 * Create a "live" job monitor from a given MessageAccessor and StatusNotifier. To be used for
	 * newly created jobs.
	 */
	public JobMonitor newJobMonitor(JobId id, MessageAccessor messageAccessor, StatusNotifier statusNotifier) {
		return new JobMonitorImpl(id, messageAccessor, statusNotifier);
	}

	private static Logger logger = LoggerFactory.getLogger(JobMonitorFactory.class);

	// We need to cache the monitors because we aren't able to create one while a job is already
	// running. Another reason for the caching is that the message storage is not guaranteed to be
	// lossless. Notably the database can currently not store the full message tree, so the messages
	// are flattened first.
	private static final Map<MessageStorage,Map<JobId,JobMonitor>> LIVE_MONITORS = new HashMap<>();

	private final Map<JobId,JobMonitor> liveMonitors;
	private final MessageStorage messageStorage;

	/**
	 * Not thread-safe.
	 */
	public JobMonitorFactory(JobStorage storage) {
		messageStorage = storage.getMessageStorage();
		if (!LIVE_MONITORS.containsKey(messageStorage)) {
			// This is the first time a JobMonitorFactory is created (for the given MessageStorage,
			// of which there should be only one).

			// Use this property only for testing! Use to configure how long messages are cached
			// before storing them in a MessageStorage (volatile of persistent).
			int timeout = Integer.valueOf(Properties.getProperty("org.daisy.pipeline.messaging.cache.buffer", "60"));
			LIVE_MONITORS.put(
				messageStorage,
				CacheBuilder.newBuilder()
				            .expireAfterAccess(timeout, TimeUnit.SECONDS)
				            .removalListener(
				                notification -> {
				                    JobMonitor mon = (JobMonitor)notification.getValue();
				                    // store buffered messages to memory or database
				                    logger.trace("Persisting messages to " + messageStorage);
				                    for (Message m : mon.getMessageAccessor().getAll())
				                        messageStorage.add(m); })
				            .<JobId,JobMonitor>build()
				            .asMap()
			);
		}
		liveMonitors = LIVE_MONITORS.get(messageStorage);
	}

	private final class JobMonitorImpl implements JobMonitor {

		private final JobMonitor monitor;

		private JobMonitorImpl(JobId id) {
			// If a monitor is cached, return it.
			if (liveMonitors.containsKey(id))
				monitor = liveMonitors.get(id);
			// Otherwise load the messages from storage (assumes the job has finished).
			else {
				liveMonitors.remove(id); // this is needed to trigger removal listener (why?)
				MessageAccessor messageAccessor = new JobMessageAccessorFromStorage(id, messageStorage);
				StatusNotifier statusNotifier = new StatusNotifier() {
					public void listen(Consumer<Job.Status> listener) {}
					public void unlisten(Consumer<Job.Status> listener) {}
				};
				monitor = new JobMonitor() {
					public MessageAccessor getMessageAccessor() {
						return messageAccessor;
					}
					public StatusNotifier getStatusUpdates() {
						return statusNotifier;
					}
				};
			}
		}

		private JobMonitorImpl(JobId id, MessageAccessor messageAccessor, StatusNotifier statusNotifier) {
			if (liveMonitors.containsKey(id))
				throw new IllegalArgumentException(); // a monitor was already registered for this job
			else {
				liveMonitors.remove(id); // this is needed to trigger removal listener (why?)
				monitor = new JobMonitor() {
					public MessageAccessor getMessageAccessor() {
						return messageAccessor;
					}
					public StatusNotifier getStatusUpdates() {
						return statusNotifier;
					}
				};
				liveMonitors.put(id, monitor);
				logger.trace("Registered JobMonitor for job " + id);
				// Keep the monitor in the cache for the time job is running. The monitor will
				// be evicted 60 seconds after the last message or status update has arrived.
				messageAccessor.listen(i -> liveMonitors.get(id));
				statusNotifier.listen(s -> liveMonitors.get(id));
			}
		}

		@Override
		public MessageAccessor getMessageAccessor() {
			return monitor.getMessageAccessor();
		}

		@Override
		public StatusNotifier getStatusUpdates() {
			return monitor.getStatusUpdates();
		}
	}
}
