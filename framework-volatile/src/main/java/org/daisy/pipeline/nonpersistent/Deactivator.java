package org.daisy.pipeline.nonpersistent;

import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.JobManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;

public class Deactivator{
	private JobManager manager;
	private static final Logger logger = LoggerFactory.getLogger(Deactivator.class);
	/**
	 * @param manager the manager to set
	 */
	public void setJobManager(JobManager manager) {
		this.manager = manager;
	}

	public void deactivate(){
		manager.deleteAll();
	}

}
