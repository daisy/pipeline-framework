package org.daisy.pipeline.job;

import org.daisy.common.xproc.XProcInput;
import org.daisy.pipeline.script.XProcScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class ExecutingJobManager.
 */
public class ExecutingJobManager extends DefaultJobManager {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(ExecutingJobManager.class);
	
	/** The executor. */
	private JobExecutionService executor = null;

	/**
	 * Activate.
	 */
	public void activate(){
		logger.trace("Activating job manager");
	}
	
	/* (non-Javadoc)
	 * @see org.daisy.pipeline.job.DefaultJobManager#newJob(org.daisy.pipeline.script.XProcScript, org.daisy.common.xproc.XProcInput, org.daisy.pipeline.job.ResourceCollection)
	 */
	public Job newJob(XProcScript script, XProcInput input,
			ResourceCollection context) {
		if (executor == null) {
			throw new IllegalStateException("Execution service unavailable");
		}
		Job job = super.newJob(script, input, context);
		executor.submit(job);
		return job;
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.job.DefaultJobManager#deleteJob(org.daisy.pipeline.job.JobId)
	 */
	@Override
	public Job deleteJob(JobId id) {
		// TODO cancel job when deleting
		return super.deleteJob(id);
	}

	/**
	 * Sets the execution service.
	 *
	 * @param executor the new execution service
	 */
	public void setExecutionService(JobExecutionService executor) {
		if (executor == null) {
			throw new IllegalArgumentException("Execution service is null");
		}
		this.executor = executor;
	}

}