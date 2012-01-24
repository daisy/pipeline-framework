package org.daisy.common.messaging;

import java.util.Date;

/**
 * Simple yet useful message definition
 */
public class Message {

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

	/** The m throwable. */
	final Throwable mThrowable;

	/** The m msg. */
	final String mMsg;

	/** The m level. */
	final Level mLevel;

	/** The m time stamp. */
	final Date mTimeStamp;

	private final int mSequence;
	/**
	 * Instantiates a new message object
	 *
	 * @param level
	 *            the level
	 * @param msg
	 *            the msg
	 * @param throwable
	 *            the throwable
	 */
	private Message(Level level, String msg, Throwable throwable,int sequence) {
		mLevel = level;
		mMsg = msg;
		mThrowable = throwable;
		mTimeStamp = new Date();
		mSequence=sequence;
	}

	/**
	 * Gets the throwable in case was defined in the constructor or null otherwise.
	 *
	 * @return the throwable
	 */
	public Throwable getThrowable() {
		return mThrowable;
	}

	/**
	 * Gets the msg
	 *
	 * @return the msg
	 */
	public String getMsg() {
		return mMsg;
	}

	/**
	 * Gets the level.
	 *
	 * @return the level
	 */
	public Level getLevel() {
		return mLevel;
	}

	/**
	 * Gets the time stamp.
	 *
	 * @return the time stamp
	 */
	public Date getTimeStamp() {
		return mTimeStamp;
	}

	public int getSequence() {
		return mSequence;
	}


	/**
	 * Builder for creating new messages
	 */
	public static final class Builder {

		/** The m throwable. */
		Throwable mThrowable;

		/** The m msg. */
		String mMsg;

		/** The m level. */
		Level mLevel;

		int mSequence;
		/**
		 * With message.
		 *
		 * @param message
		 *            the message
		 * @return the builder
		 */
		public Builder withMessage(String message) {
			mMsg = message;
			return this;
		}

		/**
		 * With level.
		 *
		 * @param level
		 *            the level
		 * @return the builder
		 */
		public Builder withLevel(Level level) {
			mLevel = level;
			return this;
		}

		/**
		 * With throwable.
		 *
		 * @param throwable
		 *            the throwable
		 * @return the builder
		 */
		public Builder withThrowable(Throwable throwable) {
			mThrowable = throwable;
			return this;
		}

		/**
		 * Builds the message based on the objects provided using the "with" methods.
		 *
		 * @return the message
		 */
		public Message build() {
			return new Message(mLevel, mMsg, mThrowable,mSequence);
		}

		public Builder withSequence(int i) {
			mSequence=i;
			return this;
		}
	}
}
