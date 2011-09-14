package org.daisy.pipeline.job;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import org.daisy.common.messaging.Message;
import org.daisy.common.messaging.MessageAccessor;

public class JobResult {
	public static class Builder {
		URI mZipFile;
		MessageAccessor mMessages;
		URI mLogFile;

		public Builder withMessageAccessor(MessageAccessor messageAccessor) {
			mMessages = messageAccessor;
			return this;
		}

		public Builder withZipFile(URI zipFile) {
			mZipFile = zipFile;
			return this;
		}
		public Builder withLogFile(URI logFile) {
			this.mLogFile=logFile;
			return this;
		}
		public JobResult build() {
			return new JobResult(mZipFile, mMessages,mLogFile);
		}

		
	}

	final URI mZipFile;
	final MessageAccessor mMessages;
	final URI mLogFile;
	private JobResult(URI zipFile, MessageAccessor messages,URI logFile) {
		super();
		mZipFile = zipFile;
		mMessages = messages;
		mLogFile=logFile;
	}

	public List<Message> getErrors() {
		if (mMessages == null)
			return new LinkedList<Message>();
		else
			return mMessages.getErrors();
	}

	public List<Message> getWarnings() {
		if (mMessages == null)
			return new LinkedList<Message>();
		else
			return mMessages.getWarnings();
	}

	public URI getZip() {
		return mZipFile;
	}
	
	public URI getLogFile(){
		return mLogFile;
	}

}
