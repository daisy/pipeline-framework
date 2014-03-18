package org.daisy.pipeline.job.priority;

import java.util.Collection;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.daisy.pipeline.job.priority.timetracking.TimeTracker;
import org.daisy.pipeline.job.priority.timetracking.TimeTrackerFactory;
/**
 * Thread pool excutor that the underlying queue supports the PriorityService interface
 * methods. It also allows to perform automatic priority updates through a time tracker .
 */
public class PriorityThreadPoolExecutor extends ThreadPoolExecutor 
                {
        private UpdatablePriorityBlockingQueue queue;
        private TimeTracker tracker;

        /**
         * Creates a new instance this class, see {@link java.util.concurrent.ThreadPoolExecutor}.
         *
         * @param corePoolSize
         * @param maximumPoolSize
         * @param keepAliveTime
         * @param unit
         * @param workQueue
         * @param tracker
         */
        PriorityThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
                        long keepAliveTime, TimeUnit unit,
                        UpdatablePriorityBlockingQueue workQueue, TimeTracker tracker) {
                super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
                this.tracker=tracker;
                this.queue = workQueue;
        }

        /** 
         * Creates a new PriorityThreadPoolExecutor of a fixed size and uses the {@link TimeTrackerFactory}
         * As time tracking mehod.
         * */
        public static  PriorityThreadPoolExecutor newFixedSizeThreadPoolExecutor(int poolSize,TimeTrackerFactory trackerFactory) {
                UpdatablePriorityBlockingQueue queue = new UpdatablePriorityBlockingQueue(); 
                TimeTracker tracker=trackerFactory.newTimeTracker(queue); 
                return new PriorityThreadPoolExecutor(poolSize,poolSize,0L,TimeUnit.MICROSECONDS,queue,tracker);
        }

        
        @Override
        protected void beforeExecute(Thread t, Runnable r) {
                super.beforeExecute(t, r);
                this.tracker.executing((PrioritizableRunnable)r);
        }

        /*
         * Just wrap the priority queue
         */

        public void moveUp(PrioritizableRunnable item) {
                this.queue.moveUp(item);

        }

        public void moveDown(PrioritizableRunnable item) {
                this.queue.moveUp(item);

        }

        public Collection<PrioritizableRunnable> asOrderedCollection() {
                return this.queue.asOrderedCollection();
        }

        public Collection<PrioritizableRunnable> asCollection(){
                return this.queue.asCollection();
        }

}
