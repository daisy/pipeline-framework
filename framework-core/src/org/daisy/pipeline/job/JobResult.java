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

		public Builder withMessageAccessor(MessageAccessor messageAccessor) {
			mMessages = messageAccessor;
			return this;
		}

		public Builder withZipFile(URI zipFile) {
			mZipFile = zipFile;
			return this;
		}

		public JobResult build() {
			return new JobResult(mZipFile, mMessages);
		}
	}

	final URI mZipFile;
	final MessageAccessor mMessages;

	private JobResult(URI zipFile, MessageAccessor messages) {
		super();
		mZipFile = zipFile;
		mMessages = messages;
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

}
