package org.daisy.pipeline.job.priority;

import com.google.common.base.Function;

public class ForwardingPrioritableRunnable extends PrioritizableRunnable {

        private PrioritizableRunnable delegate;

        public ForwardingPrioritableRunnable(PrioritizableRunnable delegate) {
                super(null,null);
                this.delegate = delegate;
        }

        @Override
        public void run() {
                this.delegate.run();
        }

        @Override
        public PriorityCalculator getPriorityCalculator() {
                return this.delegate.getPriorityCalculator();
        }

        @Override
        public long getTimestamp() {
                return this.delegate.getTimestamp();
        }

        @Override
        public synchronized double getRelativeWaitingTime() {
                return this.delegate.getRelativeWaitingTime();
        }

        @Override
        public synchronized void markDirty(boolean dirty) {
                this.delegate.markDirty(dirty);
        }

        @Override
        public synchronized boolean isDirty() {
                return this.delegate.isDirty();
        }

        @Override
        public synchronized void setRelativeWaitingTime(
                        Function<Long, Double> normalizer) {
                this.delegate.setRelativeWaitingTime(normalizer);
        }

        @Override
        public synchronized double getPriority() {
                return this.delegate.getPriority();
        }

        /**
         * @return the delegate
         */
        public PrioritizableRunnable getDelegate() {
                return delegate;
        }

}
