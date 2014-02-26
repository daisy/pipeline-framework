package org.daisy.pipeline.job.priority;

import java.util.Collection;
/**
 * Basic operations for services that give support 
 * to {@link PrioritizedRunnable} objects.
 *
 */
public interface PriorityService {
        /**
         * Moves the item up in the execution queue
         *
         * @param item
         */
        public void moveUp(PrioritizedRunnable item);

        /**
         * Moves the item down in the execution queue
         *
         * @param item
         */
        public void moveDown(PrioritizedRunnable item);

        /**
         * Returns a {@link Collection} of all the runnables order by their priority. 
         *
         * @return
         */
        public Collection<PrioritizedRunnable> asOrderedCollection();
}
