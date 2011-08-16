package org.daisy.pipeline.job;

import java.util.concurrent.ExecutorService;

import org.daisy.common.xproc.XProcEngine;

public class DefaultJobExecutionService implements JobExecutionService {

	private XProcEngine xprocEngine;
	private ExecutorService executor; 

	public void submit(final Job job) {
		executor.submit(new Runnable() {
			
			@Override
			public void run() {
				job.run(xprocEngine);
				
			}
		});
	}
}
