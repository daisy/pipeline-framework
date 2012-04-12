package org.daisy.common.messaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.daisy.common.messaging.Message.Level;

import com.google.common.collect.HashMultimap;

/**
 * This class receives message events and stores them in memory and gives access
 * to them via the accessor interface. The class that is interested in
 * processing a memoryMessage
 *
 */
public class MemoryMessageListener extends MessageAccessor implements
		MessageListener {
	/* TODO add a configuration item for message level */
	/** The m messages. */
	HashMultimap<Level, Message> mMessages = HashMultimap.create();
	List<MemoryMessage> mSeqList = new ArrayList<MemoryMessage>();
	int mSequence = 0;

	/**
	 * Stores the message
	 *
	 * @param level
	 *            the level
	 * @param str
	 *            the str
	 * @param thw
	 *            the thw
	 */
	private void store(Level level, String str, Throwable thw) {
		MemoryMessage msg = new MemoryMessage.Builder().withLevel(level)
				.withMessage(str).withThrowable(thw).withSequence(mSequence++)
				.build();
		mSeqList.add(msg);
		mMessages.put(level, msg);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.daisy.common.messaging.MessageListener#trace(java.lang.String)
	 */
	@Override
	public void trace(String msg) {
		// store(Level.TRACE, msg, null);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.daisy.common.messaging.MessageListener#trace(java.lang.String,
	 * java.lang.Throwable)
	 */
	@Override
	public void trace(String msg, Throwable throwable) {
		// ignore
		// store(Level.TRACE, msg, throwable);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.daisy.common.messaging.MessageListener#debug(java.lang.String)
	 */
	@Override
	public void debug(String msg) {

		 store(Level.DEBUG, msg, null);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.daisy.common.messaging.MessageListener#debug(java.lang.String,
	 * java.lang.Throwable)
	 */
	@Override
	public void debug(String msg, Throwable throwable) {
		 store(Level.DEBUG, msg, throwable);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.daisy.common.messaging.MessageListener#info(java.lang.String)
	 */
	@Override
	public void info(String msg) {
		store(Level.INFO, msg, null);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.daisy.common.messaging.MessageListener#info(java.lang.String,
	 * java.lang.Throwable)
	 */
	@Override
	public void info(String msg, Throwable throwable) {
		store(Level.INFO, msg, throwable);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.daisy.common.messaging.MessageListener#warn(java.lang.String)
	 */
	@Override
	public void warn(String msg) {
		store(Level.WARNING, msg, null);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.daisy.common.messaging.MessageListener#warn(java.lang.String,
	 * java.lang.Throwable)
	 */
	@Override
	public void warn(String msg, Throwable throwable) {
		store(Level.WARNING, msg, throwable);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.daisy.common.messaging.MessageListener#error(java.lang.String)
	 */
	@Override
	public void error(String msg) {
		store(Level.ERROR, msg, null);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.daisy.common.messaging.MessageListener#error(java.lang.String,
	 * java.lang.Throwable)
	 */
	@Override
	public void error(String msg, Throwable throwable) {
		store(Level.ERROR, msg, throwable);
	}


	/**
	 * Gets the messages from the given level.
	 *
	 * @param level
	 *            the level
	 * @return the messages from the level
	 */
	@Override
	protected List<Message> getMessagesFrom(Level level) {
		LinkedList<Message> msgs = new LinkedList<Message>();
		for (Level iter : Level.values()) {
			if (iter.compareTo(level) <= 0) {
				msgs.addAll(mMessages.get(iter));
			} else {
				break;
			}
		}
		return msgs;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.daisy.common.messaging.MessageListener#getAccessor()
	 */
	@Override
	public MessageAccessor getAccessor() {
		return this;
	}

	@Override
	public List<Message> getAll() {
		return new ArrayList<Message>(mSeqList);
	}

	@Override
	public MessageFilter createFilter() {

		return new MemoryMessageListener.MessageFilter();
	}

	protected class MessageFilter implements MessageAccessor.MessageFilter {
		Set<Level> mLevels=new HashSet<Level>( Arrays.asList(Level.values()));
		int mSeq;

		@Override
		public MessageFilter filterLevels(final Set<Level> levels) {
			mLevels = levels;

			return this;
		}

		@Override
		public MessageFilter fromSquence(int from) {
			mSeq = from;
			return this;
		}

		@Override
		public List<Message> getMessages() {
			List<Message> msgs = getAll();
			msgs = msgs.subList(mSeq, msgs.size());
			List<Message> filtered=new LinkedList<Message>();
				for (Message msg : msgs) {
					if (mLevels.contains(msg.getLevel())) {
						filtered.add(msg);
					}
				}

			return filtered;

		}

	}

}
