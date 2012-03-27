package org.daisy.common.messaging;

import java.util.Date;

/**
 * Simple yet useful message definition
 */
public class MemoryMessage implements Message  {



	/** The m throwable. */
	 Throwable mThrowable;

	/** The m msg. */
	String mMsg;

	/** The m level. */
	Level mLevel;

	/** The m time stamp. */
	Date mTimeStamp;

	int mSequence;
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
	protected MemoryMessage(Level level, String msg, Throwable throwable,int sequence) {
		mLevel = level;
		mMsg = msg;
		mThrowable = throwable;
		mTimeStamp = new Date();
		mSequence=sequence;
	}

	/* (non-Javadoc)
	 * @see org.daisy.common.messaging.Message#getThrowable()
	 */
	@Override
	public Throwable getThrowable() {
		return mThrowable;
	}

	/* (non-Javadoc)
	 * @see org.daisy.common.messaging.Message#getMsg()
	 */
	@Override
	public String getMsg() {
		return mMsg;
	}

	/* (non-Javadoc)
	 * @see org.daisy.common.messaging.Message#getLevel()
	 */
	@Override
	public Level getLevel() {
		return mLevel;
	}

	/* (non-Javadoc)
	 * @see org.daisy.common.messaging.Message#getTimeStamp()
	 */
	@Override
	public Date getTimeStamp() {
		return mTimeStamp;
	}

	/* (non-Javadoc)
	 * @see org.daisy.common.messaging.Message#getSequence()
	 */
	@Override
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
		public MemoryMessage build() {
			return new MemoryMessage(mLevel, mMsg, mThrowable,mSequence);
		}

		public Builder withSequence(int i) {
			mSequence=i;
			return this;
		}
	}
}
