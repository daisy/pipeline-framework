package org.daisy.pipeline.persistence.messaging.Message;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.daisy.common.base.Filter;
import org.daisy.common.messaging.Message;
import org.daisy.common.messaging.Message.Level;
import org.daisy.common.messaging.MessageAccessor;
import org.daisy.common.messaging.MessageListener;
import org.daisy.pipeline.job.JobId;

/**
 * This class receives message events and stores them in memory and gives access
 * to them via the accessor interface. The class that is interested in
 * processing a memoryMessage
 * 
 */
public class PersistentMessageListener implements MessageListener,
		MessageAccessor {

	int mSequence = 0;
	JobId mJobId;
	EntityManager mEm;

	PersistentMessageListener(JobId jId, EntityManager em) {
		this.mEm = em;
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
		EntityTransaction trans=mEm.getTransaction();
		trans.begin();
		mEm.persist(msg);
		trans.commit();
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
		// ignore for now
		// store(Level.DEBUG, msg, null);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.daisy.common.messaging.MessageListener#debug(java.lang.String,
	 * java.lang.Throwable)
	 */
	@Override
	public void debug(String msg, Throwable throwable) {
		// store(Level.DEBUG, msg, throwable);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.daisy.common.messaging.MessageAccessor#getErrors()
	 */
	@Override
	public List<Message> getErrors() {
		return getMessagesFrom(Level.ERROR);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.daisy.common.messaging.MessageAccessor#getWarnings()
	 */
	@Override
	public List<Message> getWarnings() {
		return getMessagesFrom(Level.WARNING);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.daisy.common.messaging.MessageAccessor#getInfos()
	 */
	@Override
	public List<Message> getInfos() {
		return getMessagesFrom(Level.INFO);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.daisy.common.messaging.MessageAccessor#getDebugs()
	 */
	@Override
	public List<Message> getDebugs() {
		return getMessagesFrom(Level.DEBUG);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.daisy.common.messaging.MessageAccessor#getTraces()
	 */
	@Override
	public List<Message> getTraces() {
		return getMessagesFrom(Level.TRACE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.daisy.common.messaging.MessageAccessor#getMessgages(org.daisy.common
	 * .messaging.Message.Level[])
	 */
	@Override
	public List<Message> getMessages(Level... fromLevel) {
		LinkedList<Level> set = new LinkedList<Level>();
		set.addAll(Arrays.asList(fromLevel));
		return PersistentMessage.getMessages(mEm, mJobId, 0, Arrays.asList(Level.values()));
		

	}

	/**
	 * Gets the messages from the given level.
	 * 
	 * @param level
	 *            the level
	 * @return the messages from the level
	 */
	private List<Message> getMessagesFrom(Level level) {
		
		List<Level> levels=new LinkedList<Level>();
		
		for (Level iter : Level.values()) {
			if (iter.compareTo(level) == 0) {
				levels.add(iter);
			} else {
				break;
			}
		}
		
		return PersistentMessage.getMessages(mEm, mJobId, 0, Arrays.asList(Level.values()));
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
		return PersistentMessage.getMessages(mEm, mJobId, 0, Arrays.asList(Level.values()));
	}

	@Override
	public List<Message> filtered(Filter<List<Message>>... filters) {
		List<Message> filtered = getAll();

		for (Filter<List<Message>> filter : filters) {
			filtered = filter.filter(filtered);
		}
		return filtered;
	}

}
