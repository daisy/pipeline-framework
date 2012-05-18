package org.daisy.pipeline.persistence.messaging;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.daisy.common.messaging.Message;
import org.daisy.common.messaging.Message.Level;
import org.daisy.common.messaging.MessageAccessor;
import org.daisy.pipeline.job.JobId;

public class PersistentMessageAccessor extends MessageAccessor {

	JobId jobId;

	public PersistentMessageAccessor(JobId jobId) {
		super();
		this.jobId = jobId;
	}

	@Override
	public List<Message> getAll() {
		return PersistentMessage.getMessages(jobId, 0,
				Arrays.asList(Level.values()));
	}

	@Override
	protected List<Message> getMessagesFrom(Level level) {
		List<Level> levels = new LinkedList<Level>();

		for (Level iter : Level.values()) {
			if (iter.compareTo(level) == 0) {
				levels.add(iter);
			} else {
				break;
			}
		}

		return PersistentMessage.getMessages(jobId, 0,
				Arrays.asList(Level.values()));
	}

	@Override
	public MessageFilter createFilter() {
		return new PersistentMessageAccessor.MessageFilter();
	}

	protected class MessageFilter implements MessageAccessor.MessageFilter {
		List<Level> mLevels = Arrays.asList(Level.values());
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

			return PersistentMessage.getMessages(jobId, mSeq, mLevels);

		}

	}
}
