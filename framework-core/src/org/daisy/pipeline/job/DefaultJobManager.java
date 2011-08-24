package org.daisy.pipeline.job;

import java.util.Collections;
import java.util.Map;

import org.daisy.common.xproc.XProcInput;
import org.daisy.pipeline.script.XProcScript;

public class DefaultJobManager implements JobManager {
	
	private Map<JobId, Job> jobs;

	public Job newJob(XProcScript script, XProcInput input,
			ResourceCollection context) {
		Job job = Job.newJob(script, input, context);
		jobs.put(job.getId(), job);
		return job;
	}

	@Override
	public Job newJob(XProcScript script, XProcInput input) {
		return newJob(script, input, null);
	}

	@Override
	public Iterable<Job> getJobs() {
		return Collections.unmodifiableCollection(jobs.values());
	}

	@Override
	public Job deleteJob(JobId id) {
		return jobs.remove(id);
	}

	@Override
	public Job getJob(JobId id) {
		return jobs.get(id);
	}
}
