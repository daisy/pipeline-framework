package org.daisy.pipeline.job;

import java.util.Collection;

import org.daisy.pipeline.job.priority.Prioritizable;
import org.daisy.pipeline.job.priority.PriorityThreadPoolExecutor;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

public class FilteredExecutionQueue extends DefaultExecutionQueue {

        public PriorityThreadPoolExecutor<Job> executor;

        public Predicate<Prioritizable<Job>> filter;

        /**
         * @param queue
         * @param filter
         */
        public FilteredExecutionQueue(PriorityThreadPoolExecutor<Job> executor,
                        Predicate<Prioritizable<Job>> filter) {
                super(executor);
                this.filter = filter;
        }

        @Override
        public Collection<? extends Prioritizable<Job>> asCollection() {
                return Collections2.filter(super.asCollection(),this.filter);
        }

        //faster
        protected Collection<? extends Prioritizable<Job>> nonOrdered(){
                return Collections2.filter(super.nonOrdered(),this.filter);

        }

        
}
