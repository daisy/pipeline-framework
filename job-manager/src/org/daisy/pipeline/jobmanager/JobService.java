package org.daisy.pipeline.jobmanager;

import org.daisy.pipeline.modules.converter.ConverterRunnable;
import org.osgi.framework.BundleContext;

public class JobService implements JobManager{

	public void init(BundleContext ctxt){
		
	}
	
	@Override
	public Iterable<Job> getJobList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JobID addJob(ConverterRunnable conv) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteJob(JobID id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Job getJob(JobID id) {
		// TODO Auto-generated method stub
		return null;
	}

}
