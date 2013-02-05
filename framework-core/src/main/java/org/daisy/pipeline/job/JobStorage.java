package org.daisy.pipeline.job;

public interface JobStorage  extends Iterable<Job>{
	/** It is crucial to update the reference to the job returned by this method as 
	 * the object itself maybe changed by the implementation
	 */
	public Job add(Job job); 	
	public Job remove(JobId jobId); 	
	public Job get(JobId id); 	
}
