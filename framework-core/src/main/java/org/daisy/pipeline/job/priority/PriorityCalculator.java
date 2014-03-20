package org.daisy.pipeline.job.priority;

public interface PriorityCalculator {

	double getPriority(PrioritizableRunnable runnable); 
        
}
