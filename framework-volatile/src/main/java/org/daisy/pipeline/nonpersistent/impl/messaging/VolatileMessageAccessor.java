package org.daisy.pipeline.nonpersistent.impl.messaging;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.daisy.common.messaging.Message;
import org.daisy.common.messaging.Message.Level;
import org.daisy.common.messaging.MessageAccessor;
import org.daisy.pipeline.event.ProgressMessage;
import org.daisy.pipeline.job.JobId;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class VolatileMessageAccessor extends MessageAccessor{

	private final VolatileMessageStorage storage=VolatileMessageStorage.getInstance();
	private final Iterable<MessageFilter> messages;
	private final String id;
	private final List<BiConsumer<MessageAccessor,Integer>> callbacks;
	private final BiConsumer<String,Integer> onNewMessages;

	/**
	 * @param id
	 */
	public VolatileMessageAccessor(JobId id) {
		this.id = id.toString();
		messages = storage.get(this.id);
		callbacks = new ArrayList<>();
		onNewMessages = (j,m) -> {
			if (this.id.equals(j))
				for (BiConsumer<MessageAccessor,Integer> c : callbacks)
					c.accept(VolatileMessageAccessor.this, m); };
	}

	@Override
	public List<Message> getAll() {
		return createFilter().getMessages();
	}

	@Override
	protected List<Message> getMessagesFrom(final Level level) {
		return createFilter().filterLevels(fromLevel(level)).getMessages();
	}
	
	private Set<Level> fromLevel(Level level) {
		ImmutableSet.Builder<Level> b = new ImmutableSet.Builder();
		for (Level l : Level.values())
			if (l.compareTo(level) <= 0)
				b.add(l);
		return b.build();
	}

	@Override
	public boolean delete() {
		storage.remove(id);
		return true;
	}

	@Override
	public BigDecimal getProgress() {
		BigDecimal progress;
		synchronized(ProgressMessage.MUTEX) {
			progress = storage.get(this.id).stream().map(
					m -> m.getProgress().multiply(m.getPortion())
				).reduce(
					BigDecimal.ZERO,
					(d1, d2) -> d1.add(d2)
				).min(BigDecimal.ONE);
		}
		return progress;
	}

	public void listen(BiConsumer<MessageAccessor,Integer> callback) {
		synchronized (storage) {
			if (callbacks.isEmpty())
				storage.onNewMessages.add(onNewMessages);
			callbacks.add(callback);
		}
	}

	public void unlisten(BiConsumer<MessageAccessor,Integer> callback) {
		synchronized (storage) {
			callbacks.remove(callback);
			if (callbacks.isEmpty())
				storage.onNewMessages.remove(onNewMessages);
		}
	}
	
	@Override
	public MessageFilter createFilter() {
		return new VolatileMessageFilter();
	}

	private static final Set<Level> allLevels = ImmutableSet.copyOf(Level.values());

	private class VolatileMessageFilter implements MessageFilter {
		
		private Set<Level> levels = new HashSet<Level>(allLevels);
		private Integer rangeStart = null;
		private Integer rangeEnd = null;
		private Integer greaterThan = null;

		@Override
		public MessageFilter filterLevels(final Set<Level> levels) {
			if (this.levels.size() == allLevels.size())
				this.levels = levels;
			else
				this.levels = Sets.intersection(this.levels, levels);
			return this;
		}

		@Override
		public MessageFilter greaterThan(int idx) {
			if (greaterThan == null || idx > greaterThan)
				greaterThan = idx;
			return this;
		}

		/**
		 * @param start inclusive
		 * @param end inclusive
		 * @return
		 */
		@Override
		public MessageFilter inRange(int start, int end) {
			if (start < 0)
				throw new IndexOutOfBoundsException("range start has to be 0 or greater");
			if (start > end)
				throw new IllegalArgumentException("range start is greater than end");
			if (rangeStart == null || start > rangeStart)
				rangeStart = start;
			if (rangeEnd == null || end < rangeEnd)
				rangeEnd = end;
			return this;
		}

		@Override
		public List<Message> getMessages() {
			synchronized(ProgressMessage.MUTEX) {
				return Lists.newArrayList(
					Iterables.concat(
						Iterables.transform(
							VolatileMessageAccessor.this.messages,
							(MessageFilter m) -> {
								if (greaterThan != null)
									m = m.greaterThan(greaterThan);
								if (rangeStart != null)
									m = m.inRange(rangeStart, rangeEnd);
								if (levels.size() < allLevels.size())
									m = m.filterLevels(levels);
								return m.getMessages(); })));
			}
		}
	}
}
