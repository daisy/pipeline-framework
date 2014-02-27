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
 * This class adds a layer to a forwarded {@link java.util.concurrent.PriorityBlockingQueue} that allows to update
 * the order based on a priority or via the provided methods.
 *
 * <h2>Updating the priorities</h2>
 * <ul>
 *      <li>{@link #update(Function<Void,PriorityBlockingQueue> function)}:  Updates the priorities by applying the given function sequentually to the different priorities. The function has update the {@link PrioritizedRunnable} by reference. This function is threadsafe</li>
 *      <li>{@link #moveUp(PrioritizedRunnable runnable)} allows to move up in the queue the given runnable (or a runnable inside the queue that is naturally equals to the paramater)</li>
 *      <li>{@link #moveDown(PrioritizedRunnable runnable)} allows to move up in the queue the given runnable (or a runnable inside the queue that is naturally equals to the paramater)</li>
 *
 * </ul>
 * This class is threadsafe
 * @version 1.0
 */
public class UpdatablePriorityBlockingQueue extends
                ForwardingBlockingQueue<Runnable> {


        /**
         * The forwarded priority queue
         */
        private PriorityBlockingQueue<PrioritizedRunnable> delegate;

        /**
         * Monitor that controls {@link #take()} and other blocking aspects of this class
         */ 
        private Monitor monitor = new Monitor();

        /**
         * Condition when take stops blocking. There is something in the queue
         * and there is no updating taking place.
         */
        private Monitor.Guard canTake = new Monitor.Guard(monitor) {

                @Override
                public boolean isSatisfied() {
                        return delegate.size() > 0 && !updating.get();
                }
        };

        /**
         * Condition for adding elements to this queue. Just makes sure
         * that no updating processing is being carried out.
         */
        private Monitor.Guard canAdd = new Monitor.Guard(monitor) {

                @Override
                public boolean isSatisfied() {
                        return !updating.get();
                }
        };

        /**
         * flag controlling the updating processes
         */
        AtomicBoolean updating = new AtomicBoolean(false);

        /**
         * Creates a new UpdatablePriorityBlockingQueue, it has infinite<sup>infinite<sup>infinite<sup>infinite<sup>infinite</sup></sup></sup></sup>
         * capacity.
         */
        public UpdatablePriorityBlockingQueue() {
                this.delegate = this.buildQueue();
        }

        /**
         * @return a brand new queue, it does not set it as delegate
         */
        private PriorityBlockingQueue<PrioritizedRunnable> buildQueue() {
                return new PriorityBlockingQueue<PrioritizedRunnable>(20,
                                new PrioritizedComparator());
        }


        /**
         * Updates the concurrent mechanisms when 
         * a updating process is starting.
         */
        private void enterUpdate() {
                this.updating.set(true);
                this.monitor.enter();
                //System.out.println("update: enter monitor");
        }

        /**
         * Updates the concurrent mechanisms when 
         * a updating process is finishing and re-inserts all 
         * the elements in the queue.
         */
        private void leaveUpdate() {
                this.doUpdate();
                this.updating.set(false);
                this.monitor.leave();
                //System.out.println("update: left monitor");
        }

        /**
         * Copies the delegate, cleans it and reinserts all the elements.
         * This has to be done in order to reorder the runnables according 
         * to their priority.
         */
        private void doUpdate() {
                //re-add the elements of the queue to re-sort them
                Collection<PrioritizedRunnable> aux = ImmutableList
                                .copyOf(this.delegate);
                this.delegate.clear();
                this.delegate.addAll(aux);
        }

        /**
         * Moves the task (or an object naturally equal to the task) up in queue
         * @param runnable
         */
        public synchronized void moveUp(PrioritizedRunnable runnable) {
                //first element doesn't move up
                if (runnable.equals(delegate.element())) {
                        return;
                }
                this.enterUpdate();
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
         * Moves the task (or an object naturally equal to the task) down in queue
         * @param runnable
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
        protected synchronized void update() {
                this.enterUpdate();
                this.leaveUpdate();
        }

        /**
         * Applies the function to all the elements in the queue. The function
         * must change the objects by reference. Once the function has been applied
         * the queue is reordered.
         * This function is threadsafe
         * @param function
         */
        public synchronized void update(Function<PrioritizedRunnable, Void> function) {
                this.enterUpdate();
                for (PrioritizedRunnable runnable : this.delegate) {
                        function.apply(runnable);
                }
                this.leaveUpdate();
        }

        /**
         * Returns the runnables as an immutable {@link java.util.Collection} <b>maintaining</b> the order given by the priority
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
         * Returns the runnables as an immutable {@link java.util.Collection} <b>without maintaining</b> the order given by the priority
         */
        public Collection<PrioritizedRunnable> asCollection() {
                return ImmutableList.copyOf(this.delegate);
        }

        /**
         * Returns this forwarding class delegate
         * @return
         */
        @Override
        @SuppressWarnings({ "unchecked" })
        protected BlockingQueue<Runnable> delegate() {
                return (BlockingQueue<Runnable>) (BlockingQueue<? extends Runnable>) this.delegate;
        }

        /**
         * See {@link java.util.concurrent.BlockingQueue#offer()}, This method may block if the queue 
         * is being updated.
         */
        @Override
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

        /**
         * See {@link java.util.concurrent.BlockingQueue#add()}, This method may block if the queue 
         * is being updated.
         */
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
         * See {@link java.util.concurrent.BlockingQueue#take}, it also waits for any updating 
         * operations to finish.
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
