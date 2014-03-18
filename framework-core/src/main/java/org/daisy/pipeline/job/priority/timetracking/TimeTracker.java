package org.daisy.pipeline.job.priority.timetracking;

import org.daisy.pipeline.job.priority.PrioritizableRunnable;
import org.daisy.pipeline.job.priority.UpdatablePriorityBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;

/**
 * This class maintains a buffer of the waiting time of last N tasks exectued. Once the buffer is full
 * the {@link UpdatablePriorityBlockingQueue} associated is updated with the new generated {@link TimeStats}.
 * @version
 *
 */
public class TimeTracker{
        /**
         * Buffer of waiting times.
         */
        private long[] times;
        /**
         *Buffer possition
         */
        private int counter=0;
        /**
         *Size of the buffer
         */
        final private int size;
        /**
         *Execution queue to be updated peridically
         */
        final private UpdatablePriorityBlockingQueue queue;
        /**
         *Factory of normalising functions
         */
        final private TimeFunctionFactory functionFactory ;

        private static final Logger logger = LoggerFactory.getLogger(TimeTracker.class);

        


        /**
         * Creates a new TimeTracker with a buffer of the provided size, that updates the given queue using the functions 
         * provided by the factory.
         * @param size
         * @param queue
         */
        public TimeTracker(int size, UpdatablePriorityBlockingQueue queue, TimeFunctionFactory functionFactory) {
                this.size = size;
                this.queue = queue;
                this.functionFactory=functionFactory;
                this.times= new long[size];
        }

        /**
         * Stores the waiting time of the given runnable in the buffer
         * if the buffer is full the queue is updated.
         * <br/>
         * This function is threadsafe
         * @param runnable
         */
        public synchronized void executing(PrioritizableRunnable runnable){
                //update counter and buff
                this.times[this.counter]=runnable.getTimestamp();
                this.counter++;
                //if the buffer if full 
                //send a runnable to update the queue
                if( this.counter == size){
                        this.update();                                 
                        this.counter=0;
                }
        }

        /**
         * Updates the queue 
         */
        void update(){
                logger.debug("Updating queue");
                //new stats
                TimeStats stats= new TimeStats(System.nanoTime(),TimeTracker.this.times);
                //get a new updater function
                final Function<Long,Double> timeUpdater=TimeTracker.this.functionFactory.getFunction(stats);
                //Let the queue do the work
                this.queue.update(new Function<PrioritizableRunnable, Void>() {
                        @Override
                        public Void apply(PrioritizableRunnable runnable) {
                                runnable.setRelativeWaitingTime(timeUpdater);
                                //ugly as hell but you can't intantiate void, go figure.
                                return null;
                        }
                });
        }
}
