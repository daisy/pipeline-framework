package org.daisy.pipeline.job;

import org.daisy.pipeline.clients.Client;




/**
 * The Interface JobExecutionService.
 */
public interface JobExecutionService {

	/**
	 * Submits a new job to execute.
	 *
	 * @param job the job
	 */
	public void submit(Job job);

        public ExecutionQueue getExecutionQueue();
        //TODO: merge this filter with the getQueue
        public JobExecutionService filterBy(Client client);


        
}

