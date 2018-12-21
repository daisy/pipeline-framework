package org.daisy.pipeline.event;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.function.Consumer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.MDC;

class ProgressMessageImpl extends ProgressMessage {

	final String jobId;
	private final ProgressMessageImpl parent;
	private final BigDecimal portion;
	private final List<Consumer<ProgressMessageUpdate>> callbacks;
	private final Thread thread;
	private final JobThread threadId;
	private final List<ProgressMessageImpl> children = new ArrayList<ProgressMessageImpl>();
	private final List<ProgressMessage> unmodifiableChildren = new ArrayList<ProgressMessage>();
	private final List<ProgressMessage> unmodifiableListOfUnmodifiableChildren = Collections.unmodifiableList(unmodifiableChildren);

	private boolean closed = false;
	private int closeSequence = 0;

	public static final ThreadLocal<ProgressMessage> activeBlock = new ThreadLocal<ProgressMessage>();
	public static final Map<JobThread,ProgressMessage> activeBlockInThread = new HashMap<JobThread,ProgressMessage>();

	public ProgressMessageImpl(Throwable throwable, String text, Level level, int line,
	                           int column, Date timeStamp, Integer sequence, String jobId,
	                           String file, ProgressMessageImpl parent, BigDecimal portion,
	                           List<Consumer<ProgressMessageUpdate>> callbacks) {
		super(throwable, text, level, line, column, timeStamp, sequence, jobId, file);
		this.jobId = jobId;
		this.parent = parent;
		this.portion = portion;
		this.callbacks = callbacks;
		thread = Thread.currentThread();
		if (parent != null && parent.closed)
			throw new IllegalArgumentException("Parent must be open");
		if (parent != null && !jobId.equals(parent.jobId))
			throw new IllegalArgumentException("Message must belong to same job as parent");
		ProgressMessage active = activeBlock.get();
		if (active != null && active != parent)
			throw new RuntimeException("Only one active ProgressMessage allowed in the same thread");
		activeBlock.set(this);
		if (parent == null) {
			JobThread t = new JobThread(jobId, "default");
			if (activeBlockInThread.containsKey(t)) {
				threadId = new JobThread(jobId, "thread-"+sequence);
				MDC.put("jobthread", threadId.threadId);
			} else
				threadId = t;
		}
		else if (thread == parent.thread)
			threadId = parent.threadId;
		else {
			threadId = new JobThread(jobId, "thread-"+sequence);
			MDC.put("jobthread", threadId.threadId);
		}
		activeBlockInThread.put(threadId, this);
	}

	public synchronized void close() {
		if (closed)
			throw new UnsupportedOperationException("Already closed");
		if (this != activeBlock.get()) {
			for (ProgressMessageImpl m : children)
				if (!m.closed)
					throw new UnsupportedOperationException("All children blocks must be closed before the parent can be closed");
			if (thread != Thread.currentThread())
				throw new IllegalStateException("A ProgressMessage must be created and closed in the same thread");
			else
				throw new RuntimeException("???");
		}
		closed = true;
		closeSequence = messageCounts.get(jobId);
		if (parent != null && thread == parent.thread) {
			activeBlock.set(parent);
			activeBlockInThread.put(parent.threadId, parent);
		} else {
			activeBlock.remove();
			MDC.remove("jobthread");
			activeBlockInThread.remove(threadId);
		}
		updated(closeSequence);
	}

	public synchronized ProgressMessage post(ProgressMessageBuilder message) {
		if (closed)
			throw new UnsupportedOperationException("Closed");
		synchronized(MUTEX) {
			ProgressMessage m = message.build(this);
			children.add((ProgressMessageImpl)m);
			unmodifiableChildren.add(unmodifiable(m));
			updated(m.getSequence());
			return m;
		}
	}

	boolean isOpen() {
		return !closed;
	}

	int getCloseSequence() {
		if (!closed)
			throw new UnsupportedOperationException("Not closed yet");
		else
			return closeSequence;
	}

	public String getJobId() {
		return jobId;
	}

	public BigDecimal getPortion() {
		return portion;
	}

	private BigDecimal progress = BigDecimal.ZERO;

	public BigDecimal getProgress() {
		if (closed || portion.compareTo(BigDecimal.ZERO) == 0 || progress.compareTo(BigDecimal.ONE) >= 0)
			return BigDecimal.ONE;
		progress = BigDecimal.ZERO;
		synchronized(MUTEX) {
			for (ProgressMessageImpl m : children) {
				progress = progress.add(m.closed ? m.portion : m.portion.multiply(m.getProgress()));
				if (progress.compareTo(BigDecimal.ONE) >= 0)
					return BigDecimal.ONE;
			}
		}
		return progress;
	}

	void updated(int sequence) {
		if (callbacks != null && !callbacks.isEmpty()) {
			ProgressMessageUpdate update = new ProgressMessageUpdate(this, sequence);
			for (Consumer<ProgressMessageUpdate> callback : callbacks)
				callback.accept(update); }
		if (parent != null)
			parent.updated(sequence);
	}

	/**
	 * Returns read-only (but not immutable) elements. The iterator does not implement the remove()
	 * method.
	 */
	public Iterator<ProgressMessage> __iterator() {
		return unmodifiableListOfUnmodifiableChildren.iterator();
	}

	static class JobThread {
		final String jobId;
		final String threadId;
		JobThread(String jobId, String threadId) {
			this.jobId = jobId;
			this.threadId = threadId;
		}
		@Override
		public String toString() {
			return "{" + jobId + ", " + threadId + "}";
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + jobId.hashCode();
			result = prime * result + threadId.hashCode();
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			JobThread other = (JobThread) obj;
			if (!jobId.equals(other.jobId))
				return false;
			if (!threadId.equals(other.threadId))
				return false;
			return true;
		}
	}
}
