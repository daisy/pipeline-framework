package org.daisy.pipeline.job;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.daisy.common.xproc.XProcEngine;

public class DefaultJobExecutionService implements JobExecutionService {

	private XProcEngine xprocEngine;
	
	public void setXProcEngine(XProcEngine xprocEngine) {
		// TODO make it dynamic
		this.xprocEngine = xprocEngine;
		Properties props = new Properties();
		if (System.getProperties().containsKey(XProcEngine.CONFIGURATION_FILE)){
			props.setProperty(XProcEngine.CONFIGURATION_FILE, System.getProperty(XProcEngine.CONFIGURATION_FILE));
		}
		xprocEngine.setProperties(props);
	}

	private ExecutorService executor = Executors.newCachedThreadPool(); 

	public void submit(final Job job) {
		executor.submit(new Runnable() {
			
			@Override
			public void run() {
				job.run(xprocEngine);
			}
		});
	}
}
