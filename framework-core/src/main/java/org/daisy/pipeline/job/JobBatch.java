package org.daisy.pipeline.job;

import java.util.LinkedList;


public class JobBatch extends LinkedList<Job>{
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        private JobBatchId id;

        /**
         * @param id
         */
        public JobBatch(JobBatchId id) {
                this.id = id;
        }

        /**
         * @return the id
         */
        public JobBatchId getId() {
                return id;
        }
        
}
