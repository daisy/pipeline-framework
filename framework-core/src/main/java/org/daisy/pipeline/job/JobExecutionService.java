package org.daisy.pipeline.job;

import java.util.Collection;



// TODO: Auto-generated Javadoc
/**
 * The Interface JobExecutionService.
 */
public interface JobExecutionService extends ExecutionQueue{

	/**
	 * Submits a new job to execute.
	 *
	 * @param job the job
	 */
	public void submit(Job job);

        
}

