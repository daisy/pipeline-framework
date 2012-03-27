package org.daisy.common.messaging;

import java.util.Date;

public interface Message {
	/**
	 * Message levels.
	 */
	public enum Level {
		/** The ERROR. */
		ERROR,
		/** The WARNING. */
		WARNING,
		/** The INFO. */
		INFO,
		/** The DEBUG. */
		DEBUG,
		/** The TRACE. */
		TRACE
	}
	/**
	 * Gets the throwable in case was defined in the constructor or null otherwise.
	 *
	 * @return the throwable
	 */
	public abstract Throwable getThrowable();

	/**
	 * Gets the msg
	 *
	 * @return the msg
	 */
	public abstract String getMsg();

	/**
	 * Gets the level.
	 *
	 * @return the level
	 */
	public abstract Level getLevel();

	/**
	 * Gets the time stamp.
	 *
	 * @return the time stamp
	 */
	public abstract Date getTimeStamp();

	public abstract int getSequence();

}