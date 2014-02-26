package org.daisy.pipeline.job.priority;

import com.google.common.base.Function;

/**
 * A runnable that is able to compute its own priority and supplies the element used to compute its priority
 *
 */
public abstract class PrioritizedRunnable implements Runnable{
        long timestamp;
        protected boolean dirty=true;
        protected double relativeWaitingTime;

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

        /**
         * @return the relativeWaitingTime
         */
        public double getRelativeWaitingTime() {
                return this.relativeWaitingTime;
        }

        /**
         *  marks this runnable as dirty 
         */
        public void markDirty() {
                this.dirty = true;
        }

        /**
         * Uses the normaliser to set the relative waiting time of this 
         * runnable and marks it as dirty;
         */
        public synchronized void setRelativeWaitingTime(Function<Long,Double> normalizer){
                
                this.relativeWaitingTime=normalizer.apply(this.getTimestamp());
                this.markDirty();
        }
       
}
