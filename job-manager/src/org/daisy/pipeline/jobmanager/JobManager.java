package org.daisy.pipeline.jobmanager;

import org.daisy.pipeline.modules.converter.ConverterRunnable;

public interface JobManager {
	public Iterable<Job> getJobList();
	public JobID addJob(ConverterRunnable conv);
	public boolean deleteJob(JobID id);
	public Job getJob(JobID id);
	public IDFactory getIDFactory();
	
	
}
