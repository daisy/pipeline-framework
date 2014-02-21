package org.daisy.pipeline.job.priority;

import java.util.Collection;

public interface PriorityService {
        
        public void moveUp(PrioritizedRunnable item);
        public void moveDown(PrioritizedRunnable item);
        public Collection<PrioritizedRunnable> asOrderedCollection();
}
