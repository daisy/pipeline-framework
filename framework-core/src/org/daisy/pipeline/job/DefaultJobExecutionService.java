package org.daisy.pipeline.job;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.daisy.common.xproc.XProcEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class DefaultJobExecutionService implements JobExecutionService {

	private static final Logger logger = LoggerFactory.getLogger(DefaultJobExecutionService.class);
	private XProcEngine xprocEngine;
	
	public void setXProcEngine(XProcEngine xprocEngine) {
		// TODO make it dynamic
		this.xprocEngine = xprocEngine;
	}

	private ExecutorService executor = Executors.newCachedThreadPool(); 

	public void activate(){
		logger.trace("Activating job execution service");
	}
	
	public void submit(final Job job) {
		executor.submit(new Runnable() {
			
			@Override
			public void run() {
				logger.info("Starting to log to job's log file too");
				MDC.put("jobid", job.getId().toString());
				job.run(xprocEngine);
				MDC.remove("jobid");
				logger.info("Stopping to log to job's log file");
			}
		});
	}
}
