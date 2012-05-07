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
	public abstract String getText();

	/**
	 * Gets the level.
	 *
	 * @return the level
	 */
	public abstract Level getLevel();

	/**
	 * Gets the file line.
	 *
	 * @return the level
	 */
	public abstract int getLine();

	/**
	 * Gets the file column.
	 *
	 * @return the level
	 */
	public abstract int getColumn();

	/**
	 * Gets the file.
	 *
	 * @return the level
	 */
	public abstract String getFile();



	/**
	 * Gets the time stamp.
	 *
	 * @return the time stamp
	 */
	public abstract Date getTimeStamp();

	public abstract int getSequence();

	public abstract String getJobId();
	public static interface MessageBuilder{

		public abstract MessageBuilder withThrowable(Throwable throwable);

		public abstract MessageBuilder withText(String text);

		public abstract MessageBuilder withLevel(Level level);

		public abstract MessageBuilder withLine(int line);

		public abstract MessageBuilder withColumn(int column);

		public abstract MessageBuilder withTimeStamp(Date timeStamp);

		public abstract MessageBuilder withSequence(int sequence);

		public abstract MessageBuilder withJobId(String string);

		public abstract MessageBuilder withFile(String file);
		public Message build();

	}

}