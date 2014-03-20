package org.daisy.pipeline.job.fuzzy;

import org.daisy.pipeline.job.priority.PrioritizableRunnable;
import org.daisy.pipeline.job.priority.PriorityCalculator;

import com.google.common.base.Supplier;
import com.google.common.primitives.Doubles;



/**
 * Computes its final priority from a set of priorities and 
 * the time spent in the execution queue. This computation is calculated
 * using a fuzzy {@link InferenceEngine}.
 *
 */
public class FuzzyPriorityCalculator implements PriorityCalculator{
        /**
         * Inference engine used to compute the final priority
         */
        private final InferenceEngine infereneceEngine;
        /**
         * score given by the engine for the current runnable status 
         */
        private double score;

        private Supplier<double[]> crispsSupplier;

        /**
         * @param infereneceEngine
         */
        public FuzzyPriorityCalculator(InferenceEngine infereneceEngine, Supplier<double[]> crispsSupplier) {
                this.infereneceEngine = infereneceEngine;
                this.crispsSupplier=crispsSupplier;
        }

        /**
         * Returns the score of this FuzzyRunnable computed with InfereneceEngine
         */
        @Override
        public double getPriority(PrioritizableRunnable runnable) {
                //Lazy score calcualtion and caching
                if(runnable.isDirty()){
                        double[] crispValues=Doubles.concat(new double[]{runnable.getRelativeWaitingTime()},this.crispsSupplier.get());
                        this.score=-1*this.infereneceEngine.getScore(crispValues);
                        runnable.markDirty(false);
                }
                return this.score;
        }

        /**
         * @return the crispsSupplier
         */
        public Supplier<double[]> getCrispsSupplier() {
                return crispsSupplier;
        }
}
