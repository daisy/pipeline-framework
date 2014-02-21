package org.daisy.pipeline.job.priority;

import java.util.Collection;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.daisy.pipeline.job.priority.timetracking.TimeFunctionFactory;
import org.daisy.pipeline.job.priority.timetracking.TimeTracker;
import org.daisy.pipeline.job.priority.timetracking.TimeTrackerFactory;
/**
 * Thread pool excutor that the underlying queue supports the PriorityService interface
 * methods. It also allows to perform automatic priority updates though a time tracker and a normalizing function.
 *
 *
 */
public class PriorityThreadPoolExecutor extends ThreadPoolExecutor implements
                PriorityService {
        private UpdatablePriorityBlockingQueue queue;
        private TimeTracker tracker;

        PriorityThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
                        long keepAliveTime, TimeUnit unit,
                        UpdatablePriorityBlockingQueue workQueue, TimeTracker tracker) {
                super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
                this.tracker=tracker;
                this.queue = workQueue;
        }

        /** 
         * Creates a new PriorityThreadPoolExecutor of the given size and uses the TimeTracker and normalising functions
         * returned by the factories.
         */
        public static  PriorityThreadPoolExecutor newFixedSizeThreadPoolExecutor(int poolSize,TimeTrackerFactory trackerFactory,TimeFunctionFactory normalizerFactory) {
                UpdatablePriorityBlockingQueue queue = new UpdatablePriorityBlockingQueue(); 
                TimeTracker tracker=trackerFactory.newTimeTracker(queue); 
                return new PriorityThreadPoolExecutor(poolSize,poolSize,0L,TimeUnit.MICROSECONDS,queue,tracker);
        }

        
        @Override
        protected void beforeExecute(Thread t, Runnable r) {
                super.beforeExecute(t, r);
                this.tracker.executing((PrioritizedRunnable)r);
        }

        /*
         * Just wrap the priority queue
         */

        @Override
        public void moveUp(PrioritizedRunnable item) {
                this.queue.moveUp(item);

        }

        @Override
        public void moveDown(PrioritizedRunnable item) {
                this.queue.moveUp(item);

        }

        @Override
        public Collection<PrioritizedRunnable> asOrderedCollection() {
                return this.queue.asOrderedCollection();
        }

}
