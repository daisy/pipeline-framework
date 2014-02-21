package org.daisy.pipeline.job.priority.timetracking;

public class TimeFunctions {


        public static TimeFunctionFactory newLinearTimeFunctionFactory(){
                return new LinearTimeNormalizer();
        }
}
