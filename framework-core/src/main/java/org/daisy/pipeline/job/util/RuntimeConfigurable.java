package org.daisy.pipeline.job.util;

import org.daisy.pipeline.event.EventBusSupplier;
import org.daisy.pipeline.job.JobMonitorFactory;


public interface RuntimeConfigurable  {

	public void setEventBusSupplier(EventBusSupplier eventBus);
	public void setMonitorFactory( JobMonitorFactory monitor);
}
