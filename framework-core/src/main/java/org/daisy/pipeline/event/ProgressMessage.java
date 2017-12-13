package org.daisy.pipeline.event;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.function.Consumer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.daisy.common.messaging.Message;
import org.daisy.common.messaging.MessageAccessor.MessageFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public abstract class ProgressMessage extends Message implements MessageFilter, Iterable<ProgressMessage> {
	
	private static Logger logger = LoggerFactory.getLogger(ProgressMessage.class);

	public abstract String getJobId();

	/** The total progress of this block */
	public abstract BigDecimal getProgress();
	
	/** Portion within parent */
	public abstract BigDecimal getPortion();

	public abstract void close();

	public abstract ProgressMessage post(ProgressMessageBuilder message);
	
	abstract boolean isOpen();

	/** Sequence number of an imaginary close message */
	abstract int getCloseSequence();

	/** Makes a deep copy */
	public Iterator<ProgressMessage> iterator() {
		synchronized(MUTEX) {
			return Iterators.transform(
				_iterator(),
				m -> deepCopy(m));
		}
	}

	private static ProgressMessage deepCopy(ProgressMessage message) {
		return new UnmodifiableProgressMessage(message) {
			final Iterable<ProgressMessage> children = ImmutableList.copyOf(super.iterator());
			public Iterator<ProgressMessage> iterator() {
				return children.iterator();
			}
		};
	}

	private Iterator<ProgressMessage> i = null;
	
	protected final boolean isEmpty() {
		if (i == null)
			i = __iterator();
		return !i.hasNext();
	}

	/** No deep copy, optimized */
	public final Iterator<ProgressMessage> _iterator() {
		if (i != null) {
			Iterator<ProgressMessage> ret = i;
			i = null;
			return ret;
		} else
			return __iterator();
	}

	/** No deep copy, not optimized */
	protected abstract Iterator<ProgressMessage> __iterator();

	private Iterable<ProgressMessage> singleton = null;
	protected Iterable<ProgressMessage> selfIterate() {
		if (singleton == null)
			singleton = Collections.<ProgressMessage>singleton(this);
		return singleton;
	}

	private MessageFilterImpl asMessageFilter() {
		return new MessageFilterImpl(this);
	}
	
	public MessageFilter filterLevels(Set<Level> levels) {
		return asMessageFilter().filterLevels(levels);
	}

	public MessageFilter greaterThan(int sequence) {
		return asMessageFilter().greaterThan(sequence);
	}

	public MessageFilter inRange(int start, int end) {
		return asMessageFilter().inRange(start, end);
	}

	/** Returns a view without text-less messages */
	public MessageFilter withText() {
		return asMessageFilter().withText();
	}

	public List<Message> getMessages() {
		return asMessageFilter().getMessages();
	}

	@Override
	public String toString() {
		String s = "" + getSequence();
		String children = Lists.<Message>newArrayList(this).toString();
		if (!children.equals("[]"))
			s += (" " + children);
		return s;
	}

	private ProgressMessage(Throwable throwable, String text, Level level, int line,
	                   int column, Date timeStamp, Integer sequence, String jobId, String file) {
		super(throwable, text, level, line, column, timeStamp, sequence, jobId, file);
	}

	public static final Object MUTEX = new Object();

	/** Returns the active block in the current thread */
	public static ProgressMessage getActiveBlock() {
		return activeBlock.get();
	}

	/** Returns the active block in the specified job thread */
	public static ProgressMessage getActiveBlock(String jobId, String threadId) {
		if (threadId == null)
			threadId = "default";
		return activeBlockInThread.get(new JobThread(jobId, threadId));
	}

	private static final ThreadLocal<ProgressMessage> activeBlock = new ThreadLocal<ProgressMessage>();
	private static final Map<JobThread,ProgressMessage> activeBlockInThread = new HashMap<JobThread,ProgressMessage>();

	private static class ProgressMessageImpl extends ProgressMessage {

		private final String jobId;
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

		private ProgressMessageImpl(Throwable throwable, String text, Level level, int line,
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

		/** Unmodifiable iterator, unmodifiable elements */
		public Iterator<ProgressMessage> __iterator() {
			return unmodifiableListOfUnmodifiableChildren.iterator();
		}
	}

	/** Create unmodifiable view of message */
	private static ProgressMessage unmodifiable(ProgressMessage message) {
		if (message instanceof UnmodifiableProgressMessage)
			return message;
		else
			return new UnmodifiableProgressMessage(message);
	}

	private static class UnmodifiableProgressMessage extends ProgressMessage {
		
		private final ProgressMessage message;
		
		private UnmodifiableProgressMessage(ProgressMessage message) {
			super(message.getThrowable(),
			      message.getText(),
			      message.getLevel(),
			      message.getLine(),
			      message.getColumn(),
			      message.getTimeStamp(),
			      message.getSequence(),
			      message.getJobId(),
			      message.getFile());
			this.message = message;
		}

		public String getJobId() {
			return message.getJobId();
		}

		public BigDecimal getPortion() {
			return message.getPortion();
		}

		public BigDecimal getProgress() {
			return message.getProgress();
		}

		public void close() {
			throw new UnsupportedOperationException("This is an unmodifiable view of the message");
		}

		public ProgressMessage post(ProgressMessageBuilder m) {
			throw new UnsupportedOperationException("This is an unmodifiable view of the message");
		}

		boolean isOpen() {
			return message.isOpen();
		}

		int getCloseSequence() {
			return message.getCloseSequence();
		}

		@Override
		public Iterator<ProgressMessage> __iterator() {
			return message._iterator();
		}
	}

	private static class MessageFilterImpl implements MessageFilter {

		private static final Set<Level> allLevels = ImmutableSet.copyOf(Level.values());
		
		private final ProgressMessage message;
		private Filter filter = null;
		private Set<Level> levels = new HashSet<Level>(allLevels);
		private Integer start = null;
		private Integer end = null;
		private boolean onlyWithText = false;

		private MessageFilterImpl(ProgressMessage message) {
			this.message = message;
		}

		public MessageFilter filterLevels(Set<Level> levels) {
			if (this.levels.size() == allLevels.size())
				this.levels = levels;
			else
				this.levels = Sets.intersection(this.levels, levels);
			filter = null;
			return this;
		}

		public MessageFilter greaterThan(int sequence) {
			if (start == null || sequence >= start) {
				start = sequence + 1;
				filter = null;
			}
			return this;
		}

		public MessageFilter inRange(int start, int end) {
			if (start < 0)
				throw new IndexOutOfBoundsException("range start has to be 0 or greater");
			if (start > end)
				throw new IllegalArgumentException("range start is greater than end");
			if (this.start == null || start > this.start) {
				this.start = start;
				filter = null;
			}
			if (this.end == null || end < this.end) {
				this.end = end;
				filter = null;
			}
			return this;
		}

		public MessageFilter withText() {
			if (!onlyWithText) {
				onlyWithText = true;
				filter = null;
			}
			return this;
		}

		public List<Message> getMessages() {
			if (filter == null) {
				if (start != null || end != null) {
					if (end == null)
						filter = sequenceFilter(start);
					else
						filter = sequenceFilter(start, end);
				}
				if (levels.size() < allLevels.size()) {
					filter = compose(levelFilter(levels), filter);
				}
				if (onlyWithText) {
					filter = compose(textFilter, filter);
				}
			}
			List<Message> r;
			String traceMessage;
			synchronized(MUTEX) {
				if (filter != null)
					r = Lists.<Message>newArrayList(
						Iterators.transform(
							filter.apply(message).iterator(),
							m -> deepCopy(m)));
				else
					r = Collections.<Message>singletonList(deepCopy(message));
				traceMessage = logger.isTraceEnabled()
					? (message + " --> " + r)
					: null;
			}
			// don't call logger inside synchronized block
			logger.trace(traceMessage);
			return r;
		}
	}

	/**
	 * Promote a message
	 *
	 * @param progressFactor is the portion of the hidden parent
	 */
	private static ProgressMessage promote(ProgressMessage message, final BigDecimal progressFactor) {
		if (progressFactor.compareTo(BigDecimal.ONE) < 0)
			return new UnmodifiableProgressMessage(message) {
				@Override
				public BigDecimal getPortion() {
					return progressFactor.multiply(super.getPortion()); }};
		else
			return message;
	}

	/**
	  * Returned Iterable can not be used again after message has been modified.
	  */
	private static interface Filter extends Function<ProgressMessage,Iterable<ProgressMessage>> {}

	/** Get children */
	// (m -> m) would give us a deep copy which would be wrong
	private final static Filter getChildren = m -> () -> m._iterator();

	/** Promote children */
	private final static Filter promoteChildren = message -> {
		final BigDecimal portion = message.getPortion();
		return portion.compareTo(BigDecimal.ONE) == 0
			? getChildren.apply(message)
			: Iterables.transform(
				getChildren.apply(message),
				m -> promote(m, portion));
	};
	
	/**
	 * Create deeply filtered view of message (top down)
	 *
	 * @param shallowFilter is applied on the message itself and recursively on the children
	 */
	private static Filter deepFilterTopDown(final Filter shallowFilter) {
		return new Filter() {
			private final Filter recur = compose(this, getChildren);
			public Iterable<ProgressMessage> apply(ProgressMessage message) {
				return Iterables.transform(
					shallowFilter.apply(message),
					m -> new UnmodifiableProgressMessage(m) {
						private final Iterable<ProgressMessage> children = recur.apply(m);
						@Override
						public Iterator<ProgressMessage> __iterator() {
							return children.iterator(); }});
			}
		};
	}

	/**
	 * Create deeply filtered view of message (bottom up)
	 *
	 * @param shallowFilter is applied on leaf messages and recursively on the parents
	 */
	private static Filter deepFilterBottomUp(final Filter shallowFilter) {
		return new Filter() {
			private final Filter recur = compose(this, getChildren);
			public Iterable<ProgressMessage> apply(ProgressMessage message) {
				return shallowFilter.apply(
					new UnmodifiableProgressMessage(message) {
						private final Iterable<ProgressMessage> children = recur.apply(message);
						@Override
						public Iterator<ProgressMessage> __iterator() {
							return children.iterator(); }});
			}
		};
	}
	
	private static Filter sequenceFilter(final int start) {
		return sequenceFilter(start, Integer.MAX_VALUE);
	}
	
	/**
	 * Hide messages that are not within the given range and that have no descendants within that range.
	 */
	private static Filter sequenceFilter(final int start, final int end) {
		return deepFilterBottomUp(
			m -> isLessOrEqual(start, m.getSequence(), end)
			     || (!m.isOpen() && isLessOrEqual(start, m.getCloseSequence(), end))
			     || !m.isEmpty()
				? m.selfIterate()
				: empty
		);
	}

	/**
	 * Hide messages that are not in certains levels and promote their children.
	 */
	private static Filter levelFilter(final Set<Level> levels) {
		return deepFilterBottomUp(
			m -> levels.contains(m.level)
				? m.selfIterate()
				: promoteChildren.apply(m)
		);
	}

	/**
	 * Hide messages without text and promote their children.
	 */
	private static Filter textFilter = deepFilterBottomUp(
		m -> m.getText() != null
			? m.selfIterate()
			: promoteChildren.apply(m)
	);

	private static final Iterable<ProgressMessage> empty = Optional.<ProgressMessage>absent().asSet();

	private static Filter compose(final Filter g, final Filter f) {
		if (f == null)
			return g;
		else if (g == null)
			return f;
		else
			return m -> Iterables.concat(
				Iterables.transform(f.apply(m), g));
	}

	private static <T extends Comparable<T>> boolean isEqual(T... values) {
		T prev = null;
		for (T v : values) {
			if (prev != null && prev.compareTo(v) != 0)
				return false;
			prev = v; }
		return true;
	}

	private static <T extends Comparable<T>> boolean isLess(T... values) {
		T prev = null;
		for (T v : values) {
			if (prev != null && prev.compareTo(v) >= 0)
				return false;
			prev = v; }
		return true;
	}

	private static <T extends Comparable<T>> boolean isLessOrEqual(T... values) {
		T prev = null;
		for (T v : values) {
			if (prev != null && prev.compareTo(v) > 0)
				return false;
			prev = v; }
		return true;
	}

	private static <T extends Comparable<T>> boolean isGreater(T... values) {
		T prev = null;
		for (T v : values) {
			if (prev != null && prev.compareTo(v) <= 0)
				return false;
			prev = v; }
		return true;
	}

	public static class ProgressMessageBuilder extends AbstractMessageBuilder<ProgressMessage,ProgressMessageBuilder> {

		private BigDecimal portion;
		private List<Consumer<ProgressMessageUpdate>> callbacks;

		public ProgressMessageBuilder withProgress(BigDecimal progress) {
			if (progress.compareTo(BigDecimal.ZERO) < 0 || progress.compareTo(BigDecimal.ONE) > 0)
				throw new IllegalArgumentException("progress must be a number between 0 and 1");
			this.portion = progress;
			return this;
		}

		ProgressMessageBuilder onUpdated(Consumer<ProgressMessageUpdate> callback) {
			if (callbacks == null)
				callbacks = new ArrayList<Consumer<ProgressMessageUpdate>>();
			callbacks.add(callback);
			return this;
		}

		private ProgressMessage build(ProgressMessageImpl parent) {
			if (parent != null) {
				if (jobId == null)
					withJobId(parent.jobId);
				else if (!jobId.equals(parent.jobId))
					throw new IllegalArgumentException("Message must belong to same job as parent");
				if (portion == null)
					portion = BigDecimal.ZERO;
			} else if (portion == null)
				portion = BigDecimal.ONE;
			return new ProgressMessageImpl(throwable, text, level, line, column,
					timeStamp, null, jobId, file, parent, portion,
					callbacks != null ? new ArrayList<Consumer<ProgressMessageUpdate>>(callbacks) : null);
		}
		
		public ProgressMessage build() {
			return build(null);
		}

		public ProgressMessageBuilder self() {
			return this;
		}
	}

	private static class JobThread {
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
