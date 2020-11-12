package org.daisy.pipeline.job;

import org.daisy.common.xproc.XProcMonitor;

import com.google.common.eventbus.EventBus;

public interface JobMonitor extends XProcMonitor {

	/**
	 * Get the {@link EventBus} that this job posts {@link StatusMessage status update events}
	 * to. The caller can subscribe to these events using {@link EventBus#register(Object)}.
	 */
	public EventBus getEventBus();

}
