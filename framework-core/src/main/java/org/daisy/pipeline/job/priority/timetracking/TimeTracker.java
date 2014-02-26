package org.daisy.pipeline.job.priority.timetracking;

import org.daisy.pipeline.job.priority.PrioritizedRunnable;
import org.daisy.pipeline.job.priority.UpdatablePriorityBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;

public class TimeTracker{
        private long[] times;
        private int size;
        private UpdatablePriorityBlockingQueue queue;
        private TimeFunctionFactory functionFactory ;
        private int counter=0;

        private static final Logger logger = LoggerFactory.getLogger(TimeTracker.class);

        


        /**
         * @param size
         * @param queue
         */
        public TimeTracker(int size, UpdatablePriorityBlockingQueue queue, TimeFunctionFactory functionFactory) {
                this.size = size;
                this.queue = queue;
                this.functionFactory=functionFactory;
                this.times= new long[size];
        }

        public synchronized void executing(PrioritizedRunnable runnable){
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

        void update(){
                logger.debug("Updating queue");
                TimeStats stats= new TimeStats(System.nanoTime(),TimeTracker.this.times);
                final Function<Long,Double> timeUpdater=TimeTracker.this.functionFactory.getFunction(stats);
                this.queue.update(new Function<PrioritizedRunnable, Void>() {
                        @Override
                        public Void apply(PrioritizedRunnable runnable) {
                                runnable.setRelativeWaitingTime(timeUpdater);
                                //ugly as hell but you can't intantiate void, go figure.
                                return null;
                        }
                });
        }
}
