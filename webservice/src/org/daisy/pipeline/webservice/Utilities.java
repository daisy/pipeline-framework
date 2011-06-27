package org.daisy.pipeline.webservice;

import java.util.Iterator;

import org.daisy.pipeline.DaisyPipelineContext;
import org.daisy.pipeline.jobmanager.Job;
import org.daisy.pipeline.jobmanager.JobManager;

public class Utilities {
	
	public static Job getJob(String jobId, DaisyPipelineContext context) {
		// TODO see if there's a way to use JobManager.getJob(..)
		JobManager jobManager = context.getJobManager();
		Iterator<Job> it = jobManager.getJobList().iterator();
		while(it.hasNext()) {
			Job job = it.next();
			if (job.getId().getID() == jobId) {
				return job;
			}
		}
		return null;
	}
}
