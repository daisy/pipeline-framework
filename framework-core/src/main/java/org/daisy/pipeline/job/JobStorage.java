package org.daisy.pipeline.job;

public interface JobStorage  extends Iterable<JobId>{
	public void add(Job job); 	
	public Job remove(JobId jobId); 	
	public Job get(JobId id); 	
}
