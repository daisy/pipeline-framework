package org.daisy.pipeline.job.priority;

public interface Prioritizable<T>  {
       /**
        * Returns the priority.
        */
        public abstract double getPriority();

       /**
        * Returns the timestamp.
        */
        public long getTimestamp();

        public double getRelativeWaitingTime();
        
        public T prioritySource();
}
