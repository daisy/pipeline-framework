package org.daisy.pipeline.job;

import java.util.Collection;

public interface ExecutionQueue {
        public void moveUp(JobId id);

        public void moveDown(JobId id);

        public void cancel(JobId id);

        public Collection<PrioritizedJob> getQueue();
}
