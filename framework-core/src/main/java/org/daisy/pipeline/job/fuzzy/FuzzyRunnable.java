package org.daisy.pipeline.job.fuzzy;

import org.daisy.pipeline.job.priority.PrioritizableRunnable;

import com.google.common.base.Function;
import com.google.common.primitives.Doubles;


/**
 * Runnable that computes its final priority from a set of priorities and 
 * the time spent in the execution queue. This computation is calculated
 * using a fuzzy {@link InferenceEngine}.
 *
 */
public abstract class FuzzyRunnable extends PrioritizableRunnable{
        /**
         * Inference engine used to compute the final priority
         */
        private final InferenceEngine infereneceEngine;
        /**
         * score given by the engine for the current runnable status 
         */
        private double score;

        /**
         * @param infereneceEngine
         */
        public FuzzyRunnable(Runnable runnable,InferenceEngine infereneceEngine) {
                super(runnable);
                this.infereneceEngine = infereneceEngine;
        }

        /**
         * Returns the crisp values (in american english chips, I mean, real values) from the priorities 
         */
        public abstract double[] getPriorities();

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
        public synchronized double getScore(){
                //Lazy score calcualtion and caching
                if(this.dirty){
                        double[] crispValues=Doubles.concat(new double[]{this.getRelativeWaitingTime()},this.getPriorities());
                        this.score=this.getInfereneceEngine().getScore(crispValues);
                        this.dirty=false;
                }
                return this.score;
        }
}
