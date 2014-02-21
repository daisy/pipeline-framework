package org.daisy.pipeline.job.priority.timetracking;

import org.daisy.pipeline.job.priority.timetracking.TimeStats;

import com.google.common.base.Function;

public interface TimeFunctionFactory  {

        public Function<Long,Double> getFunction(TimeStats stats);
        
}
