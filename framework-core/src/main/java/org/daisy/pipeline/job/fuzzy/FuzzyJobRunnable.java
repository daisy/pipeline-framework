package org.daisy.pipeline.job.fuzzy;

import org.daisy.pipeline.job.Job;

/**
 * Wraps a job into a fuzzy runnable computing its final priority
 * using the job's priority and its client priority
 */
public class FuzzyJobRunnable extends FuzzyRunnable {
        final private Job job;
        /**
         *Delegated task
         */
        final private Runnable runnable;
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
                super(infereneceEngine);
                this.job=job;
                this.runnable=jobTask;
        }

        @Override
        public void run() {
                this.runnable.run();

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
        
}
