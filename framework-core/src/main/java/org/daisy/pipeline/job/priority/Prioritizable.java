package org.daisy.pipeline.job.priority;

public interface Prioritizable  {
       /**
        * Returns the priority.
        */
        public abstract double getPriority();

       /**
        * Returns the timestamp.
        */
        public long getTimestamp();

        public double getRelativeWaitingTime();
}
