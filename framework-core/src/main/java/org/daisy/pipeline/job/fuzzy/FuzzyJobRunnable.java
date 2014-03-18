package org.daisy.pipeline.job.fuzzy;

import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.PrioritizedJob;

/**
 * Wraps a job into a fuzzy runnable computing its final priority
 * using the job's priority and its client priority
 */
public class FuzzyJobRunnable extends FuzzyRunnable implements PrioritizedJob {
        final private Job job;
        /**
         * Says if the priority of this runnable has been forced
         */
        private boolean forced;
        /**
         * The (forced) priority 
         */
        private double priority;

        /**
         * Creates a new FuzzyJobRunnable, the actual running task is delegated to the jobTask
         * @param job
         * @param jobTask
         * @param infereneceEngine
         */
        public FuzzyJobRunnable(Job job, Runnable jobTask,InferenceEngine infereneceEngine) {
                super(jobTask,infereneceEngine);
                this.job=job;
        }

        /**
         * The crisp values for the job priority, a normalised version of the job and the client prioties. 
         */
        @Override
        public double[] getPriorities() {
                return new double[]{this.job.getContext().getClient().getPriority().asDouble(),
                        this.job.getPriority().asDouble()};
        }

        @Override
        public double getPriority() {
                if(this.forced){
                        return priority;
                }else{
                        //the lower the value the higher the prio
                        //go figure
                        return -1*this.getScore();
                }
        }

        @Override
        public double forcePriority(double priority) {
                this.forced=true;
                this.priority=priority;
                return this.priority;
        }

        @Override
        public Job get() {
                return this.job;
        }
        
}
