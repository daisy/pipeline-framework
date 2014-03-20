package org.daisy.pipeline.job.fuzzy;

import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.priority.PrioritizableRunnable;
import org.daisy.pipeline.job.priority.PriorityCalculator;

import com.google.common.primitives.Doubles;

public class FuzzyJobPriorityCalculator  implements PriorityCalculator{
        
        final private Job job;
        final private InferenceEngine infereneceEngine;
        private double score;


        /**
         * Creates a new FuzzyJobRunnable, the actual running task is delegated to the jobTask
         * @param job
         * @param jobTask
         * @param infereneceEngine
         */
        public FuzzyJobPriorityCalculator(Job job,InferenceEngine infereneceEngine) {
                this.job=job;
                this.infereneceEngine=infereneceEngine;
        }

        /**
         * The crisp values for the job priority, a normalised version of the job and the client prioties. 
         */
        public double[] getPriorities() {
                return new double[]{this.job.getContext().getClient().getPriority().asDouble(),
                        this.job.getPriority().asDouble()};
        }


        @Override
        public synchronized double getPriority(PrioritizableRunnable runnable) {
                return this.getScore(runnable);
        }

        /**
         *
         * @return the infereneceEngine
         */
        public InferenceEngine getInfereneceEngine() {
                return infereneceEngine;
        }
       

        /**
         * Returns the score of this FuzzyRunnable computed with InfereneceEngine
         */
        protected double getScore(PrioritizableRunnable runnable){
                //Lazy score calcualtion and caching
                if(runnable.isDirty()){
                        double[] crispValues=Doubles.concat(new double[]{runnable.getRelativeWaitingTime()},this.getPriorities());
                        this.score=this.getInfereneceEngine().getScore(crispValues);
                        runnable.markDirty(false);
                }
                return this.score;
        }
}
