package org.daisy.pipeline.job;

import java.util.Collection;

import org.daisy.pipeline.job.priority.PriorityThreadPoolExecutor;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

public class FilteredExecutionQueue extends DefaultExecutionQueue{

        public PriorityThreadPoolExecutor executor;

        public Predicate<PrioritizedJob> filter;

        /**
         * @param queue
         * @param filter
         */
        public FilteredExecutionQueue(PriorityThreadPoolExecutor executor,
                        Predicate<PrioritizedJob> filter) {
                super(executor);
                this.filter = filter;
        }

        @Override
        public Collection<PrioritizedJob> asCollection() {
                return Collections2.filter(super.asCollection(),this.filter);
        }

        //faster
        protected Collection<PrioritizedJob> nonOrdered(){
                return Collections2.filter(super.nonOrdered(),this.filter);

        }

        
}
