package org.daisy.pipeline.job.priority;


import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ForwardingBlockingQueue;
import com.google.common.util.concurrent.Monitor;

/**
 * This class adds a layer to a forwarded {@link java.util.concurrent.PriorityBlockingQueue} that allows to update
 * the order based on a priority or via the provided methods.
 *
 * <h2>Updating the priorities</h2>
 * <ul>
 *      <li>{@link #update(Function<Void,PriorityBlockingQueue> function)}:  Updates the priorities by applying the given function sequentually to the different priorities. The function has update the {@link PrioritizableRunnable} by reference. This function is threadsafe</li>
 *      <li>{@link #swap(PrioritizableRunnable runnable)} allows to swap two elements in the queue</li>
 *
 * </ul>
 * This class is threadsafe
 * @version 1.0
 */
public class UpdatablePriorityBlockingQueue<T> extends ForwardingBlockingQueue<Runnable> { 
        /**
         * The forwarded priority queue
         */
        private WrappingPriorityQueue<PrioritizableRunnable<T>,SwappingPriority<T>> delegate;

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

        private Function<SwappingPriority<T>, PrioritizableRunnable<T>> unwrapFunction = new Function<SwappingPriority<T>, PrioritizableRunnable<T>>() {
                @Override
                public PrioritizableRunnable<T> apply(SwappingPriority<T> arg) {
                        return arg.getDelegate();
                }
        };

        private Function<PrioritizableRunnable<T>,SwappingPriority<T> > wrapFunction = new Function<PrioritizableRunnable<T>,SwappingPriority<T>>() {
                @Override
                public SwappingPriority<T> apply(PrioritizableRunnable<T> arg) {
                        return new SwappingPriority<T>(arg);
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
        private WrappingPriorityQueue<PrioritizableRunnable<T>,SwappingPriority<T>> buildQueue() {
                PriorityBlockingQueue<SwappingPriority<T>> queue= new PriorityBlockingQueue<SwappingPriority<T>>(20,
                                new PrioritizableComparator());
                return new WrappingPriorityQueue<PrioritizableRunnable<T>,SwappingPriority<T>>(queue,wrapFunction,unwrapFunction);
        }


        /**
         * Updates the concurrent mechanisms when 
         * a updating process is starting.
         */
        private void enterUpdate() {
                this.updating.set(true);
                this.monitor.enter();
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
        }

        /**
         * Copies the delegate, cleans it and reinserts all the elements.
         * This has to be done in order to reorder the runnables according 
         * to their priority.
         */
        private void doUpdate() {
                //re-add the elements of the queue to re-sort them
                Collection<SwappingPriority<T>> aux = ImmutableList
                        .copyOf(this.delegate.wrapped());
                this.delegate.clear();
                this.delegate.addAllBypass(aux);
        }


        protected Optional<SwappingPriority<T>> tryFind(final PrioritizableRunnable<T> runnable){
                return Iterables.tryFind(this.delegate.wrapped(), new Predicate<SwappingPriority<T>>() {

                        @Override
                        public boolean apply(SwappingPriority<T> e) {
                               return e.getDelegate().equals(runnable); 

                        }});
        }
        /**
         * Swap the priorities of both PrioritizableRunnable to change the 
         * order in the queue
         * @param runnable
         */
        public synchronized void swap(PrioritizableRunnable<T> runnable1,PrioritizableRunnable<T> runnable2) {
                this.enterUpdate();
                Optional<SwappingPriority<T>> node1=this.tryFind(runnable1);
                Optional<SwappingPriority<T>> node2=this.tryFind(runnable2);
                //one of them doesn't exsist
                if (!node1.isPresent() ||  !node2.isPresent()){
                        this.leaveUpdate();
                        return;
                }
                //swap the overriders 
                PrioritizableRunnable<T> aux=node1.get().getOverrider();
                node1.get().setOverrider(node2.get().getOverrider());
                node2.get().setOverrider(aux);

                this.leaveUpdate();

        }


        /**
         * Applies the function to all the elements in the queue. The function
         * must change the objects by reference. Once the function has been applied
         * the queue is reordered.
         * This function is threadsafe
         * @param function
         */
        public synchronized void update(Function<PrioritizableRunnable<T>, Void> function) {
                this.enterUpdate();
                for (PrioritizableRunnable<T> runnable : this.delegate) {
                        function.apply(runnable);
                }
                this.leaveUpdate();
        }

        /**
         * Returns the runnables as an immutable {@link java.util.Collection} <b>maintaining</b> the order given by the priority
         */
        public Collection<PrioritizableRunnable<T>> asOrderedCollection() {
                List<SwappingPriority<T>> list= Lists.newLinkedList(this.delegate.wrapped());
                Collections.sort(list, new PrioritizableComparator());
                return Collections2.transform(list,this.unwrapFunction);
        }


        /**
         * Returns the runnables as an immutable {@link java.util.Collection} <b>without maintaining</b> the order given by the priority
         */
        public Collection<PrioritizableRunnable<T>> asCollection() {
                return ImmutableList.copyOf(this.delegate.unwrap());
        }

        /**
         * Returns this forwarding class delegate
         * @return
         */
        @Override
        @SuppressWarnings({ "unchecked" }) //controlled environment not (terribly) dangerous
        protected BlockingQueue<Runnable> delegate() {
                return (BlockingQueue<Runnable>) 
                        (BlockingQueue<? extends Runnable>)
                        this.delegate;
        }

        /**
         * See {@link java.util.concurrent.BlockingQueue#offer()}, This method may block if the queue 
         * is being updated.
         */
        @SuppressWarnings("unchecked")
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
                res = this.delegate.offer((PrioritizableRunnable<T>) o);
                //System.out.println("offer: left monitor");
                monitor.leave();
                return res;
        }

        /**
         * See {@link java.util.concurrent.BlockingQueue#add()}, This method may block if the queue 
         * is being updated.
         */
        @SuppressWarnings("unchecked")
		@Override
        public synchronized boolean add(Runnable element) {
                boolean res;
                try {
                        monitor.enterWhen(canAdd);
                } catch (InterruptedException e) {
                        monitor.leave();
                        throw new RuntimeException(e);
                }
                res=this.delegate.add((PrioritizableRunnable<T>)element);
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
class SwappingPriority<T> extends
ForwardingPrioritableRunnable<T> {

        PrioritizableRunnable<T> overrider;

        public SwappingPriority(PrioritizableRunnable<T> delegate,PrioritizableRunnable<T> overrider) {
                super(delegate);
                this.overrider=overrider;
        }
        public SwappingPriority(PrioritizableRunnable<T> delegate) {
                super(delegate);
                this.overrider=delegate;
        }

        @Override
        public double getPriority() {
                return this.overrider.getPriority();
        }

        /**
         * @return the overrider
         */
        public PrioritizableRunnable<T> getOverrider() {
                return overrider;
        }

        /**
         * @param overrider the overrider to set
         */
        public void setOverrider(PrioritizableRunnable<T> overrider) {
                this.overrider = overrider;
        }
}

