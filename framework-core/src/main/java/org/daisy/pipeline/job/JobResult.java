package org.daisy.pipeline.job;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import org.daisy.common.messaging.Message;
import org.daisy.common.messaging.MessageAccessor;

// TODO: Auto-generated Javadoc
/**
 * The Class JobResult.
 */
public class JobResult {

	/**
	 * The Class Builder.
	 */
	public static class Builder {

		/** The m zip file. */
		URI mZipFile;

		/** The m messages. */
		MessageAccessor mMessages;

		/** The m log file. */
		URI mLogFile;

		/**
		 * With message accessor.
		 *
		 * @param messageAccessor the message accessor
		 * @return the builder
		 */
		public Builder withMessageAccessor(MessageAccessor messageAccessor) {
			mMessages = messageAccessor;
			return this;
		}

		/**
		 * With zip file.
		 *
		 * @param zipFile the zip file
		 * @return the builder
		 */
		public Builder withZipFile(URI zipFile) {
			mZipFile = zipFile;
			return this;
		}

		/**
		 * With log file.
		 *
		 * @param logFile the log file
		 * @return the builder
		 */
		public Builder withLogFile(URI logFile) {
			mLogFile=logFile;
			return this;
		}

		/**
		 * Builds the job result object.
		 *
		 * @return the job result
		 */
		public JobResult build() {
			return new JobResult(mZipFile, mMessages,mLogFile);
		}


	}

	/** The zip file. */
	final URI mZipFile;

	/** The  messages. */
	final MessageAccessor mMessages;

	/** The  log file. */
	final URI mLogFile;

	/**
	 * Instantiates a new job result.
	 *
	 * @param zipFile the zip file
	 * @param messages the messages
	 * @param logFile the log file
	 */
	private JobResult(URI zipFile, MessageAccessor messages,URI logFile) {
		super();
		mZipFile = zipFile;
		mMessages = messages;
		mLogFile=logFile;
	}

	/**
	 * Gets the errors produced during the pipeline execution.
	 *
	 * @return the errors
	 */
	public List<Message> getErrors() {
		if (mMessages == null) {
			return new LinkedList<Message>();
		} else {
			return mMessages.getErrors();
		}
	}

	/**
	 * Gets the warnings produced during the pipeline execution.
	 *
	 * @return the warnings
	 */
	public List<Message> getWarnings() {
		if (mMessages == null) {
			return new LinkedList<Message>();
		} else {
			return mMessages.getWarnings();
		}
	}

	/**
	 * Gets the zip.
	 *
	 * @return the zip
	 */
	public URI getZip() {
		return mZipFile;
	}

	/**
	 * Gets the log file.
	 *
	 * @return the log file
	 */
	public URI getLogFile(){
		return mLogFile;
	}

}
