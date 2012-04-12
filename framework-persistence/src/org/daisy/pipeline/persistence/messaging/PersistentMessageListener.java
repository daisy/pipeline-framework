package org.daisy.pipeline.persistence.messaging;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.daisy.common.messaging.Message;
import org.daisy.common.messaging.Message.Level;
import org.daisy.common.messaging.MessageAccessor;
import org.daisy.common.messaging.MessageListener;
import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.persistence.DaisyEntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class receives message events and stores them in memory and gives access
 * to them via the accessor interface. The class that is interested in
 * processing a memoryMessage
 * 
 */
public class PersistentMessageListener extends MessageAccessor implements
		MessageListener {
	private static final Logger logger = LoggerFactory
			.getLogger(PersistentMessageListener.class);

	int mSequence = 0;
	JobId mJobId;

	public PersistentMessageListener(JobId jId) {

		this.mJobId = jId;
	}

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

		Message msg = new PersistentMessage.Builder().withLevel(level)
				.withMessage(str).withThrowable(thw).withSequence(mSequence++)
				.withJobId(mJobId).build();
		EntityManager em = DaisyEntityManagerFactory.createEntityManager();
		EntityTransaction trans = em.getTransaction();
		trans.begin();
		logger.debug("Thread - " + Thread.currentThread().getId());
		em.persist(msg);
		trans.commit();
		em.close();
		// em.flush();

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
	protected List<Message> getMessagesFrom(Level level) {

		List<Level> levels = new LinkedList<Level>();

		for (Level iter : Level.values()) {
			if (iter.compareTo(level) == 0) {
				levels.add(iter);
			} else {
				break;
			}
		}

		return PersistentMessage.getMessages(mJobId, 0,
				Arrays.asList(Level.values()));
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
		return PersistentMessage.getMessages(mJobId, 0,
				Arrays.asList(Level.values()));
	}

	@Override
	public MessageFilter createFilter() {
		// TODO Auto-generated method stub
		return new PersistentMessageListener.MessageFilter();
	}
	protected class MessageFilter implements MessageAccessor.MessageFilter {
		List<Level> mLevels= Arrays.asList(Level.values());
		int mSeq;

		@Override
		public MessageFilter filterLevels(final Set<Level> levels) {
			mLevels = new LinkedList<Message.Level>(levels);

			return this;
		}

		@Override
		public MessageFilter fromSquence(int from) {
			mSeq = from;
			return this;
		}

		@Override
		public List<Message> getMessages() {
		
			return PersistentMessage.getMessages(mJobId, mSeq, mLevels);

		}

	}
}
