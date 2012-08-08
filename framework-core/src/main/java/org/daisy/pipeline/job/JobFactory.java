package org.daisy.pipeline.job;

import java.io.IOException;

import org.daisy.common.xproc.XProcInput;
import org.daisy.pipeline.script.XProcScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobFactory {
	//singleton
	private static JobFactory instance=null;
	private JobMonitorFactory monitorFactory;
	private static final Logger logger = LoggerFactory.getLogger(JobFactory.class);
	public JobFactory(){
		if (instance==null) {
			instance=this;
		}
	}
	public static JobFactory getInstance(){
		new JobFactory();
		return instance;
	}

	public void setJobMonitorFactory(JobMonitorFactory monitorFactory){
		logger.debug("setting monitor factory");
		this.monitorFactory=monitorFactory;
	}

	/**
	 * Creates a new job attached to a context.
	 *
	 * @param script the script
	 * @param input the input
	 * @param context the context
	 * @return the job
	 */
	public Job newJob(XProcScript script, XProcInput input,
			ResourceCollection context) {
		// TODO check arguments
		JobId id = JobIdFactory.newId();
		// FIXME "common path"+id.toString

		try {

			IOBridge bridge = new IOBridge(id);
			XProcInput resolvedInput = bridge.resolve(script, input, context);
			if(monitorFactory==null) {
				throw new IllegalStateException("No monitor factory");
			}
			JobMonitor monitor=monitorFactory.newJobMonitor(id);
			// TODO validate input
			//return new Job(id, script, resolvedInput, bridge,monitor);
			return null;
		} catch (IOException e) {
			throw new RuntimeException("Error resolving pipeline info", e);
		}
	}
}
