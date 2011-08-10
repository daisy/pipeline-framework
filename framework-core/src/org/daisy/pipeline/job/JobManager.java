package org.daisy.pipeline.job;

import org.daisy.commons.xproc.XProcInput;
import org.daisy.commons.xproc.io.ResourceCollection;
import org.daisy.pipeline.script.XProcScript;

public class JobManager {
	JobExecutionService jobExecutionService;

	public Job newJob(XProcScript script, XProcInput input,
			ResourceCollection context) {
		Job job = Job.newJob(script, input, context);
		jobExecutionService.addJob(job);
		return job;
	}
}
