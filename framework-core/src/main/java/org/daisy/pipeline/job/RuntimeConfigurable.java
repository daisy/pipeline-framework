package org.daisy.pipeline.job;

import org.daisy.pipeline.event.EventBusSupplier;


public interface RuntimeConfigurable  {

	public void setEventBusSupplier(EventBusSupplier eventBus);
	public void setMonitorFactory( JobMonitorFactory monitor);
}
