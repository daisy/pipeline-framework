package org.daisy.pipeline.job.priority;

public interface PriorityCalculator<T> {

	public double getPriority(PrioritizableRunnable<T> runnable); 
	public T prioritySource(); 
        
}
