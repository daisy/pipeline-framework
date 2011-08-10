package org.daisy.pipeline.job;

import org.daisy.commons.xproc.XProcEngine;

public class JobExecutionService {
	XProcEngine engine;
	public void addJob(Job job) {
		job.run(engine);
	}
}
