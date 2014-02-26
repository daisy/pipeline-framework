package org.daisy.pipeline.job.priority;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ForwardingBlockingQueue;
import com.google.common.util.concurrent.Monitor;

/**
 * This class adds a layer to a forwarded BlockingQueue that allows to update
 * the order based on a priority
 *
 */
public class UpdatablePriorityBlockingQueue extends
                ForwardingBlockingQueue<Runnable> {

        private PriorityBlockingQueue<PrioritizedRunnable> delegate;
        private Monitor monitor = new Monitor();
        private Monitor.Guard canTake = new Monitor.Guard(monitor) {

                @Override
                public boolean isSatisfied() {
                        return delegate.size() > 0 && !updating.get();
                }
        };
        private Monitor.Guard canAdd = new Monitor.Guard(monitor) {

                @Override
                public boolean isSatisfied() {
                        return !updating.get();
                }
        };

        AtomicBoolean updating = new AtomicBoolean(false);
        //AtomicInteger takes=new AtomicInteger(0);

        /**
         * Creates a new UpdatablePriorityBlockingQueue
         */
        public UpdatablePriorityBlockingQueue() {
                this.delegate = this.buildQueue();
        }

        private PriorityBlockingQueue<PrioritizedRunnable> buildQueue() {
                return new PriorityBlockingQueue<PrioritizedRunnable>(20,
                                new PrioritizedComparator());
        }

        private void enterUpdate() {
                this.updating.set(true);
                this.monitor.enter();
                //System.out.println("update: enter monitor");
        }

        private void leaveUpdate() {
                this.doUpdate();
                this.updating.set(false);
                this.monitor.leave();
                //System.out.println("update: left monitor");
        }

        private void doUpdate() {
                //re-add the elements of the queue to re-sort them
                Collection<PrioritizedRunnable> aux = ImmutableList
                                .copyOf(this.delegate);
                this.delegate.clear();
                this.delegate.addAll(aux);
        }

        /**
         * Moves up the task by forcing its priority
         */
        public synchronized void moveUp(PrioritizedRunnable runnable) {
                this.enterUpdate();
                //first element doesn't move up
                if (runnable.equals(delegate.element())) {
                        return;
                }
                //get element and previous elements
                PrioritizedRunnable iter, prev, prevToPrev;
                iter = prev = prevToPrev = null;
                //no random access to queues...
                for (PrioritizedRunnable p : this.delegate) {
                        prevToPrev = prev;
                        prev = iter;
                        iter = p;
                        if (p.equals(runnable)) {//done
                                break;
                        }

                }
                double forcedPriority = 0;
                //not found
                if (!iter.equals(runnable)) {
                        return;
                } else if (prevToPrev == null) {//force the priority upper than next
                        forcedPriority = prev.getPriority() - 1;
                } else { //put it before prio2+(prio2-prio1)/2
                        forcedPriority = prev.getPriority()
                                        + (prevToPrev.getPriority() - prev.getPriority()) / 2;
                }
                iter.forcePriority(forcedPriority);
                this.leaveUpdate();

        }

        /**
         * Moves up the task by forcing its priority
         */
        public synchronized void moveDown(PrioritizedRunnable runnable) {
                this.enterUpdate();
                //get element and next 2 elements
                PrioritizedRunnable iter, next, nextToNext;
                iter = next = nextToNext = null;
                //no random access to queues...
                for (PrioritizedRunnable p : this.delegate) {
                        iter = next;
                        next = nextToNext;
                        nextToNext = p;
                        if (iter != null && iter.equals(runnable)) {
                                break;
                        }
                }

                double forcedPriority = 0;
                //not found
                if (iter != null && iter.equals(runnable)) {
                        //iter->next->nextToNext
                        //=> iter.p= next.p+(next.p-nextToNext.p/2)
                        forcedPriority = next.getPriority()
                                        - (next.getPriority() - nextToNext.getPriority()) / 2;
                } else if (next != null && next.equals(runnable)) {
                        //runnable is the second last
                        //next->nextToNext  (next is acutally the element we are looking for)
                        //=> next.p= nextToNext.p-1 so now is the last
                        forcedPriority = ((PrioritizedRunnable) nextToNext).getPriority() + 1;
                } else {
                        //no update is necesary
                        return;
                }

                ((PrioritizedRunnable) iter).forcePriority(forcedPriority);

                this.leaveUpdate();
        }

        /**
         * Reevaluates the priorities
         */
        public synchronized void update() {
                this.enterUpdate();
                this.leaveUpdate();
        }

        /**
         * Reevaluates the priorities after exectuing the runnable obtject.
         * This method provides thread-safty for external priority changes
         */
        public synchronized void update(Function<PrioritizedRunnable, Void> function) {
                this.enterUpdate();
                for (PrioritizedRunnable runnable : this.delegate) {
                        function.apply(runnable);
                }
                this.leaveUpdate();
        }

        /**
         * Returns the runnables as a collection <b>maintaining</b> the order given by the priority
         */
        public Collection<PrioritizedRunnable> asOrderedCollection() {
                //TODO use Collections
                PrioritizedRunnable[] arr;
                synchronized (this.delegate) {
                        arr = new PrioritizedRunnable[this.delegate.size()];
                        this.delegate.toArray(arr);
                }
                Arrays.sort(arr, new PrioritizedComparator());
                return ImmutableList.copyOf(Arrays.asList(arr));
        }

        /**
         * Returns the list as a collection <b>without maintaining</b> the order given by the priority
         */
        public Collection<PrioritizedRunnable> asCollection() {
                return ImmutableList.copyOf(this.delegate);
        }

        @Override
        @SuppressWarnings({ "unchecked" })
        protected BlockingQueue<Runnable> delegate() {
                return (BlockingQueue<Runnable>) (BlockingQueue<? extends Runnable>) this.delegate;
        }

        //@Override
        public synchronized boolean offer(Runnable o) {
                boolean res;
                try {
                        monitor.enterWhen(canAdd);
                } catch (InterruptedException e) {
                        monitor.leave();
                        throw new RuntimeException(e);
                }
                //System.out.println("offer: entered monitor");
                res = this.delegate.offer((PrioritizedRunnable) o);
                //System.out.println("offer: left monitor");
                monitor.leave();
                return res;
        }

        @Override
        public synchronized boolean add(Runnable element) {
                boolean res;
                try {
                        monitor.enterWhen(canAdd);
                } catch (InterruptedException e) {
                        monitor.leave();
                        throw new RuntimeException(e);
                }
                //System.out.println("add: entered monitor");
                res=this.delegate.add((PrioritizedRunnable)element);
                //System.out.println("add: left monitor");
                monitor.leave();
                return res;
        }

        /**
         * As in the blocking queue but it also waits for any updating 
         * operations to finish
         *
         */
        @Override
        public Runnable take() throws InterruptedException {
                //int num=this.takes.incrementAndGet();
                //System.out.println("Take("+num+"): before mon");
                monitor.enterWhen(canTake);
                //System.out.println("Take("+num+"): entered monitor");
                Runnable res=this.delegate.poll();
                monitor.leave();
                //System.out.println("Take("+num+"): left monitor");
                return res;
        }





}
