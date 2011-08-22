package org.daisy.pipeline.job;

import org.daisy.common.xproc.XProcInput;
import org.daisy.pipeline.script.XProcScript;

public interface JobManager {

	public Job newJob(XProcScript script, XProcInput input,
			ResourceCollection context);

	public Job newJob(XProcScript script, XProcInput input);

	public Iterable<Job> getJobs();

	public Job deleteJob(JobId id);

	public Job getJob(JobId id);
}
