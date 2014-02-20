package org.daisy.pipeline.job.priority;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

/**
 * A runnable that is able to compute its own priority and supplies the element used to compute its priority
 *
 */
public abstract class PrioritizedRunnable implements Runnable{
        long timestamp;

        public PrioritizedRunnable(){
                timestamp=System.nanoTime();
        }

       /**
        * Returns the priority of this runnable using the normalizer to normilise the 
        * runnable time-stamp
        */
        public abstract double getPriority();


        /** 
         * Forces the priority of this task to be a certain value
         *
         */
        public abstract double forcePriority(double priority);

        /**
         * @return the timestamp
         */
        public long getTimestamp() {
                return timestamp;
        }

       
}
