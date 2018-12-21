package org.daisy.pipeline.event;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

import org.daisy.common.messaging.Message;
import org.daisy.common.messaging.MessageAccessor.MessageFilter;

import org.slf4j.Logger;

public abstract class ProgressMessage extends Message implements MessageFilter, Iterable<ProgressMessage> {
	
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

	private Logger asLogger;

	public Logger asLogger() {
		if (asLogger == null)
			asLogger = new ProgressMessageLogger(this);
		return asLogger;
	}

	/** Makes a deep copy */
	public Iterator<ProgressMessage> iterator() {
		synchronized(MUTEX) {
			return Iterators.transform(
				_iterator(),
				m -> deepCopy(m));
		}
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

	ProgressMessage(Throwable throwable, String text, Level level, int line,
	                int column, Date timeStamp, Integer sequence, String jobId, String file) {
		super(throwable, text, level, line, column, timeStamp, sequence, jobId, file);
	}

	public static final Object MUTEX = new Object();

	/** Returns the active block in the current thread */
	public static ProgressMessage getActiveBlock() {
		return ProgressMessageImpl.activeBlock.get();
	}

	/** Returns the active block in the specified job thread */
	public static ProgressMessage getActiveBlock(String jobId, String threadId) {
		if (threadId == null)
			threadId = "default";
		return ProgressMessageImpl.activeBlockInThread.get(new ProgressMessageImpl.JobThread(jobId, threadId));
	}
	
	static ProgressMessage deepCopy(ProgressMessage message) {
		return new UnmodifiableProgressMessage(message) {
			final Iterable<ProgressMessage> children = ImmutableList.copyOf(super.iterator());
			public Iterator<ProgressMessage> iterator() {
				return children.iterator();
			}
		};
	}

	/** Create unmodifiable view of message */
	static ProgressMessage unmodifiable(ProgressMessage message) {
		if (message instanceof UnmodifiableProgressMessage)
			return message;
		else
			return new UnmodifiableProgressMessage(message);
	}

	static class UnmodifiableProgressMessage extends ProgressMessage {
		
		private final ProgressMessage message;
		
		UnmodifiableProgressMessage(ProgressMessage message) {
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
}
