package org.daisy.pipeline.persistence;

import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.job.JobMonitor;
import org.daisy.pipeline.job.JobMonitorFactory;

public class PersistentJobMonitorFactory implements JobMonitorFactory{

	@Override
	public JobMonitor newJobMonitor(JobId id) {
		return new PersistentJobMonitor(id);
	}

}
