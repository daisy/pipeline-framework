package org.daisy.pipeline.job.priority.timetracking;

import org.daisy.pipeline.job.priority.UpdatablePriorityBlockingQueue;


public class TimeTrackerFactory{

        int size;
        TimeFunctionFactory functionFactory;


        /**
         * @param size
         * @param functionFactory
         */
        public TimeTrackerFactory(int size, TimeFunctionFactory functionFactory) {
                this.size = size;
                this.functionFactory = functionFactory;
        }

        public static TimeTrackerFactory newFactory(int size,TimeFunctionFactory functionFactory){
                return new TimeTrackerFactory(size,functionFactory);
        }


        public TimeTracker newTimeTracker(UpdatablePriorityBlockingQueue queue){
                return new TimeTracker(this.size,queue,this.functionFactory);
        }

}
