package org.daisy.pipeline.job;

import org.daisy.common.xproc.XProcInput;
import org.daisy.pipeline.script.XProcScript;

public class ExecutingJobManager extends DefaultJobManager {

	private JobExecutionService executor = null;

	public Job newJob(XProcScript script, XProcInput input,
			ResourceCollection context) {
		if (executor == null) {
			throw new IllegalStateException("Execution service unavailable");
		}
		Job job = super.newJob(script, input, context);
		executor.submit(job);
		return job;
	}

	@Override
	public Job deleteJob(JobId id) {
		// TODO cancel job when deleting
		return super.deleteJob(id);
	}

	public void setExecutionService(JobExecutionService executor) {
		if (executor == null) {
			throw new IllegalArgumentException("Execution service is null");
		}
		this.executor = executor;
	}

}