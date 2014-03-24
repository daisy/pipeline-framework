package org.daisy.pipeline.job.priority;


import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.daisy.pipeline.job.priority.timetracking.TimeTracker;
import org.daisy.pipeline.job.priority.timetracking.TimeTrackerFactory;

/**
 * Thread pool excutor that the underlying queue supports the PriorityService interface
 * methods. It also allows to perform automatic priority updates through a time tracker .
 */
public class PriorityThreadPoolExecutor<T> extends ThreadPoolExecutor 
                {
        private TimeTracker tracker;
        //private static final Logger logger = LoggerFactory.getLogger(PriorityThreadPoolExecutor.class);

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
                        UpdatablePriorityBlockingQueue<T> workQueue, TimeTracker tracker) {
                super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
                this.tracker=tracker;
        }

        /** 
         * Creates a new PriorityThreadPoolExecutor of a fixed size and uses the {@link TimeTrackerFactory}
         * As time tracking mehod.
         * */
        public static <T> PriorityThreadPoolExecutor<T> newFixedSizeThreadPoolExecutor(int poolSize,TimeTrackerFactory trackerFactory) {
                UpdatablePriorityBlockingQueue<T> queue = new UpdatablePriorityBlockingQueue<T>(); 
                TimeTracker tracker=trackerFactory.newTimeTracker(queue); 
                return new PriorityThreadPoolExecutor<T>(poolSize,poolSize,0L,TimeUnit.MICROSECONDS,queue,tracker);
        }

        
        @Override
        protected void beforeExecute(Thread t, Runnable r) {
                super.beforeExecute(t, r);
                this.tracker.executing();
        }

        @SuppressWarnings("unchecked")
		public UpdatablePriorityBlockingQueue<T> getUpdatableQueue(){
                return (UpdatablePriorityBlockingQueue<T>) this.getQueue();
        }

}
