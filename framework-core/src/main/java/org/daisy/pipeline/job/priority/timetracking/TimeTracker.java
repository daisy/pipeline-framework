package org.daisy.pipeline.job.priority.timetracking;

import org.daisy.pipeline.job.priority.FuzzyRunnable;
import org.daisy.pipeline.job.priority.PrioritizedRunnable;
import org.daisy.pipeline.job.priority.UpdatablePriorityBlockingQueue;

import com.google.common.base.Function;

public class TimeTracker{
        private long[] times;
        private int size;
        private UpdatablePriorityBlockingQueue queue;
        private TimeFunctionFactory functionFactory ;
        private int counter=0;

        Runnable timeUpdater = new Runnable(){
                @Override
                public void run(){
                        TimeStats stats= new TimeStats(System.nanoTime(),TimeTracker.this.times);
                        Function<Long,Double> function=TimeTracker.this.functionFactory.getFunction(stats);
                        for(PrioritizedRunnable runnable:TimeTracker.this.queue.asCollection()){
                                runnable.setRelativeWaitingTime(function);
                        }

                }

        };
        


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

        public void executing(PrioritizedRunnable runnable){
                //update counter and buff
                this.times[counter]=runnable.getTimestamp();
                counter++;
                //if the buffer if full 
                //send a runnable to update the queue
                if( counter == size){
                        this.update();                                 
                        counter=0;
                }
        }

        private void update(){
                this.queue.update(timeUpdater);
        }
}
