package org.daisy.pipeline.job.priority;

import org.daisy.pipeline.job.Job;

public class FuzzyJobRunnable extends FuzzyRunnable {
        private Job job;
        private Runnable runnable;
        private boolean forced;
        double priority;

        public FuzzyJobRunnable(Job job, Runnable jobTask,InferenceEngine infereneceEngine) {
                super(infereneceEngine);
                this.job=job;
                this.runnable=jobTask;
        }

        @Override
        public void run() {
                this.runnable.run();

        }

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
