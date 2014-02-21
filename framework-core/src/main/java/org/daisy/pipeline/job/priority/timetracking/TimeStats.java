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

        /**
         * @return the times referenced to the reference instant 
         */
        public long[] getReferencedTimes() {
                long[] refd=new long[this.times.length];
                for (int i=0;i<this.times.length;i++){
                        refd[i]=this.reference(this.times[i]);
                }
                return refd;
        }

        /**
         * References the given time 
         */
        public long reference(long l){
                return this.reference-l;
        }

}
