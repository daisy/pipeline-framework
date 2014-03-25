package org.daisy.pipeline.job.priority;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.ForwardingBlockingQueue;

public class WrappingPriorityQueue<T, K extends T> extends ForwardingBlockingQueue<T> {

        private PriorityBlockingQueue<K> delegate;
        private Function<T,K> wrapFunction;
        private Function<K,T> unwrapFunction;

        /**
         * @param delegate
         */
        public WrappingPriorityQueue(PriorityBlockingQueue<K> delegate,Function<T,K> wrapFunction,Function<K,T> unwrapFunction ) {
                this.delegate = delegate;
                this.wrapFunction=wrapFunction;
                this.unwrapFunction=unwrapFunction;
        }

        @SuppressWarnings({ "unchecked" })//this never fails, it's just a view
        public PriorityBlockingQueue<T> delegate(){
                return (PriorityBlockingQueue<T>)this.delegate;
        }

        public Iterable<T> unwrap(){
                return Collections2.transform(this.delegate,unwrapFunction);
        }

        public Iterable<K> wrapped(){
                return this.delegate;
        }

        private K wrap( T element ){
                return this.wrapFunction.apply(element);
        }
        public T unwrap( K element ){
                return this.unwrapFunction.apply(element);
        }

        @Override
        public boolean add(T e) {
                return this.delegate.add(this.wrap(e));
        }

        @Override
        public boolean offer(T e) {
                return this.delegate.offer(this.wrap(e));
        }

        @Override
        public T peek() {
                return this.unwrap(this.delegate.peek());
        }

        @Override
        public T poll() {
                return this.unwrap(this.delegate.poll());
        }

        @Override
        public void put(T e) {
                this.delegate.put(this.wrap(e));
        }

        @Override
        public T take() throws InterruptedException {
                return this.unwrap(this.delegate.take());
        }

        @Override
        public boolean offer(T e, long timeout, TimeUnit unit)
                        throws InterruptedException {
                return this.delegate.offer(this.wrap(e), timeout, unit);
        }

        @Override
        public int drainTo(Collection<? super T> c) {
                int size=this.delegate.size();
                c.addAll(Collections2.transform(this.delegate,this.unwrapFunction));
                this.delegate.clear();
                return size;

        }

        @Override
        public int drainTo(Collection<? super T> c, int maxElements) {
                int i=0;
                while(i<maxElements && this.size()>0){
                        c.add(this.poll());
                }
                return i;
        }

        @Override
        public T poll(long timeout, TimeUnit unit) throws InterruptedException {
                return this.unwrap(this.delegate.poll(timeout, unit));
        }

        @Override
        public boolean addAll(Collection<? extends T> collection) {
                for(T t:collection){
                        this.delegate.add(this.wrap(t));
                }
                return true;
        }

        public boolean addAllBypass(Collection<K> collection) {
                return this.delegate.addAll(collection);
        }

        @Override
        public Iterator<T> iterator() {
                return Iterables.transform(this.delegate,this.unwrapFunction).iterator();
        }

        @Override
        public T element() {
                return this.unwrap(this.delegate.element());
        }

        @Override
        public T remove() {
                return this.unwrap(this.delegate.remove());
        }


}
