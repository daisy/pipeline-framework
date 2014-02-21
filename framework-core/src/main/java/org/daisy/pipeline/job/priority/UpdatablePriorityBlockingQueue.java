package org.daisy.pipeline.job.priority;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ForwardingBlockingQueue;

/**
 * This class adds a layer to a forwarded BlockingQueue that allows to update 
 * the order based on a priority
 *
 */
public class UpdatablePriorityBlockingQueue  extends
                ForwardingBlockingQueue<Runnable> {

        private PriorityBlockingQueue<PrioritizedRunnable> delegate;

        /**
         * Creates a new UpdatablePriorityBlockingQueue 
         */
        public UpdatablePriorityBlockingQueue() {
                this.delegate=this.buildQueue();
        }

        private PriorityBlockingQueue<PrioritizedRunnable> buildQueue(){
                return new PriorityBlockingQueue<PrioritizedRunnable>(20,new PrioritizedComparator());
        }
        /**
         * Moves up the task by forcing its priority
         */
        public  synchronized void moveUp(PrioritizedRunnable runnable){
                //first element doesn't move up
                if (runnable.equals(delegate.element())){
                        return;
                }
                //get element and previous elements
                PrioritizedRunnable iter,prev,prevToPrev;
                iter=prev=prevToPrev=null;
                //no random access to queues...
                for(PrioritizedRunnable p:this.delegate){
                        prevToPrev=prev;
                        prev=iter;
                        iter=p;
                        if (p.equals(runnable)){//done
                                break;
                        }

                }
                double forcedPriority=0;
                //not found 
                if (!iter.equals(runnable)){
                        return;
                }else if(prevToPrev==null){//force the priority upper than next
                        forcedPriority= prev.getPriority()-1;
                }else{ //put it before prio2+(prio2-prio1)/2
                        forcedPriority= prev.getPriority()+
                                ( prevToPrev.getPriority()- prev.getPriority())/2;
                }
                iter.forcePriority(forcedPriority);
                this.update();


        }

        /**
         * Moves up the task by forcing its priority
         */
        public synchronized void moveDown(PrioritizedRunnable runnable){
                //get element and next 2 elements
                PrioritizedRunnable iter,next,nextToNext;
                iter=next=nextToNext=null;
                //no random access to queues...
                for(PrioritizedRunnable p:this.delegate){
                        iter=next;
                        next=nextToNext;
                        nextToNext=p;
                        if(iter!=null && iter.equals(runnable)){
                                break;
                        }
                }

                double forcedPriority=0;
                //not found 
                if (iter!=null && iter.equals(runnable)){
                        //iter->next->nextToNext
                        //=> iter.p= next.p+(next.p-nextToNext.p/2)
                        forcedPriority= next.getPriority()-
                                ( next.getPriority()- nextToNext.getPriority())/2;
                }else if( next!=null && next.equals(runnable) ){
                        //runnable is the second last
                        //next->nextToNext  (next is acutally the element we are looking for)
                        //=> next.p= nextToNext.p-1 so now is the last
                        forcedPriority=((PrioritizedRunnable) nextToNext).getPriority()+1;
                }else{ 
                        //no update is necesary
                        return;
                }

                ((PrioritizedRunnable) iter).forcePriority(forcedPriority);

                this.update();
        }

        /**
         * Reevaluates the priorities
         */
        public synchronized void update(){
                PriorityBlockingQueue<PrioritizedRunnable> aux= buildQueue();
                aux.addAll(this.delegate);
                this.delegate=aux;//().addAll(aux);
        }

        /**
         * Reevaluates the priorities after exectuing the runnable obtject.
         * This method provides thread-safty for external priority changes 
         */
        public synchronized void update(Runnable runnable){
                runnable.run();
                this.update();
        }

        /**
         * Returns the runnables as a collection <b>maintaining</b> the order given by the priority
         */
        public synchronized Collection<PrioritizedRunnable> asOrderedCollection(){
                PrioritizedRunnable []arr = new PrioritizedRunnable[this.delegate.size()];
                this.delegate.toArray(arr);
                Arrays.sort(arr,new PrioritizedComparator()); 
                return ImmutableList.copyOf(Arrays.asList(arr));
        }

        /**
         * Returns the list as a collection <b>without maintaining</b> the order given by the priority
         */
        public synchronized Collection<PrioritizedRunnable> asCollection(){
                return ImmutableList.copyOf(this.delegate);
        }

        @Override
        @SuppressWarnings({"unchecked"})
        protected synchronized BlockingQueue<Runnable> delegate() {
                return (BlockingQueue<Runnable>)(BlockingQueue<? extends Runnable>) this.delegate;
        }
}
