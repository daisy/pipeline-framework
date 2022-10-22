package org.daisy.pipeline.job;

import java.util.Collection;

import org.daisy.common.priority.Prioritizable;

public interface JobQueue {

        public void moveUp(JobId id);

        public void moveDown(JobId id);

        public void cancel(JobId id);

        /**
         * @return the (0-based) position in the queue of the specified job. Returns <code>-1</code>
         *         if the job is not in the queue.
         */
        public int getPositionInQueue(JobId id);

        public Collection<? extends Prioritizable<Job>> asCollection();

}
