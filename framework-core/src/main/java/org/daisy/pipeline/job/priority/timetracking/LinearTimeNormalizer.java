package org.daisy.pipeline.job.priority.timetracking;

import com.google.common.base.Function;

public class LinearTimeNormalizer implements TimeFunctionFactory {
        /**
         * Returns a function that normalises a given number accordingly
         * to the stats using the formula
         * x_n=(x-min)/(max-min)
         */
        @Override
        public Function<Long, Double> getFunction(final TimeStats stats) {
                //calculate the max and min of the referrenced times
                final long[] minMax=this.findMinMax(stats.getReferencedTimes());
                return new Function<Long, Double>() {

                        @Override
                        public Double apply(Long l) {
                                return ((double)(stats.reference(l)-minMax[0]))/((double)minMax[1]-minMax[0]);
                        }};
        }


        long[] findMinMax(long vals[]){
                long min=Long.MAX_VALUE;
                long max=Long.MIN_VALUE;
                for (long l:vals){
                        if(min>l){
                                min=l;
                        }
                        if(max<l){
                                max=l;
                        }
                }
                return new long[]{min,max};
        }
        
}
