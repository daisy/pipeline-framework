package org.daisy.pipeline.job;

import org.daisy.pipeline.event.EventBusProvider;


public interface RuntimeConfigurable  {

	public void setEventBusProvider(EventBusProvider eventBus);
	public void setMonitorFactory( JobMonitorFactory monitor);
}
