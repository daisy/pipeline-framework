package org.daisy.pipeline.job.priority.timetracking;

import org.daisy.pipeline.job.priority.UpdatablePriorityBlockingQueue;

/**
 * Factory of {@link TimeTracker} objects, it allows to configure the to be created object and delays its association with the executing queue.
 */
public final class TimeTrackerFactory{
        
        /**
         * The size of the buffer
         */
        final private int size;
        /**
         *The time normaliser factory
         */
        final private TimeFunctionFactory functionFactory;


        /**
         * Creates a new TimeTrackerFactory 
         * @param size
         * @param functionFactory
         */
        private TimeTrackerFactory(int size, TimeFunctionFactory functionFactory) {
                this.size = size;
                this.functionFactory = functionFactory;
        }

        /**
         * Instantiates a new TimeTrackerFactory that will create TimeTrackers with the given buffer size and the function factory.
         */
        public static TimeTrackerFactory newFactory(int size,TimeFunctionFactory functionFactory){
                return new TimeTrackerFactory(size,functionFactory);
        }


        /**
         * Returns a new TimeTracker associated with the updatable queue.
         *
         * @param queue
         * @return
         */
        public TimeTracker newTimeTracker(UpdatablePriorityBlockingQueue queue){
                return new TimeTracker(this.size,queue,this.functionFactory);
        }

}
