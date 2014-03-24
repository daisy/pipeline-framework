package org.daisy.pipeline.job;


import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.daisy.pipeline.job.priority.PrioritizableRunnable;
import org.daisy.pipeline.job.priority.PriorityThreadPoolExecutor;
import org.daisy.pipeline.job.priority.UpdatablePriorityBlockingQueue;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class DefaultExecutionQueue implements ExecutionQueue {

        PriorityThreadPoolExecutor executor;

        /**
         * @param queue
         */
        public DefaultExecutionQueue(PriorityThreadPoolExecutor executor) {
                this.executor= executor;
        }

        @Override
        public void moveUp(JobId id) {
                Optional<PrioritizedJob> ided=this.find(id);
                if(!ided.isPresent()){
                        return;
                }
                Optional<PrioritizedJob> prev=this.findPrevious(ided.get());
                if(!prev.isPresent()){
                        return;
                }
                this.getQueue()
                        .swap((PrioritizableRunnable)ided.get(),(PrioritizableRunnable)prev.get());

        }

        @Override
        public void moveDown(JobId id) {
                Optional<PrioritizedJob> ided=this.find(id);
                if(!ided.isPresent()){
                        return;
                }
                Optional<PrioritizedJob> next=this.findNext(ided.get());
                if(!next.isPresent()){
                        return;
                }
                this.getQueue()
                        .swap((PrioritizableRunnable)ided.get(),(PrioritizableRunnable)next.get());
        }

        @Override
        public void cancel(JobId id) {
                Optional<PrioritizedJob> ided=this.find(id);
                if(!ided.isPresent()){
                        return;
                }
                this.executor.remove((PrioritizableRunnable)ided.get());
        }

        @Override
        public Collection<PrioritizedJob> asCollection(){
                return Collections2.transform(this.getQueue().asOrderedCollection(),
                                new Function<PrioritizableRunnable, PrioritizedJob>() {
                                        @Override
                                        public PrioritizedJob apply(PrioritizableRunnable runnable) {
                                                return (PrioritizedJob)runnable;
                                        }
                });

        }

        //faster
        protected Collection<PrioritizedJob> nonOrdered(){
                return Collections2.transform(this.getQueue().asCollection(),
                                new Function<PrioritizableRunnable, PrioritizedJob>() {
                                        @Override
                                        public PrioritizedJob apply(PrioritizableRunnable runnable) {
                                                return (PrioritizedJob)runnable;
                                        }
                });

        }

        Optional<PrioritizedJob> find(final JobId id){
                return Iterables.tryFind(this.nonOrdered(),
                                new Predicate<PrioritizedJob>() {
                                        @Override
                                        public boolean apply(PrioritizedJob pJob) {
                                                return pJob.getJob().getId().equals(id);
                                        }
                });
        }

        Optional<PrioritizedJob> findNext(PrioritizedJob job){
                return this.findNext(job.getJob().getId(),this.asCollection());
        }

        private Optional<PrioritizedJob> findNext(final  JobId id, Collection<PrioritizedJob> jobs){
                return Iterables.tryFind(jobs,
                                new Predicate<PrioritizedJob>() {
                                        boolean isNext=false; 
                                        @Override
                                        public boolean apply(PrioritizedJob pJob) {
                                                if(isNext){
                                                        return true;
                                                }
                                                isNext=pJob.getJob().getId().equals(id);
                                                return false;

                                        }
                });
        }

        Optional<PrioritizedJob> findPrevious(PrioritizedJob job){
                List<PrioritizedJob> reverse=Lists.newLinkedList(this.asCollection());
                Collections.reverse(reverse);
                return this.findNext(job.getJob().getId(),reverse);
        }

        /**
         * @return the executor
         */
        protected UpdatablePriorityBlockingQueue getQueue() {
                return executor.getUpdatableQueue();
        }

        
}
