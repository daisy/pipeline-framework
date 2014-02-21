package org.daisy.pipeline.job.priority.timetracking;

public class TimeStats{
        long reference; 
        long[] times;

        /**
         * @param reference
         * @param times
         */
        TimeStats(long reference, long[] times) {
                this.reference = reference;
                this.times = times;
        }

        /**
         * @return the reference
         */
        public long getReference() {
                return this.reference;
        }

        /**
         * @return the reference
         */
        public long[] getTimes() {
                return this.times;
        }

}
