package org.daisy.common.messaging;

import java.util.Date;


public class DefaultMessage implements Message {
	private final Throwable throwable;
	private final String text;
	private final Level level;
	private final int line;
	private final int column;
	private final Date timeStamp;
	private final int sequence;
	private final String jobId;
	private final String file;

	private DefaultMessage(Throwable throwable, String text, Level level,
			int line, int column, Date timeStamp, int sequence, String jobId,
			String file) {
		this.throwable = throwable;
		this.text = text;
		this.level = level;
		this.line = line;
		this.column = column;
		this.timeStamp = timeStamp;
		this.sequence = sequence;
		this.jobId = jobId;
		this.file = file;
	}

	@Override
	public Throwable getThrowable() {
		return throwable;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public Level getLevel() {
		return level;
	}

	@Override
	public int getLine() {
		return line;
	}

	@Override
	public int getColumn() {
		return column;
	}

	@Override
	public Date getTimeStamp() {
		return timeStamp;
	}

	@Override
	public int getSequence() {
		return sequence;
	}

	@Override
	public String getJobId() {
		return jobId;
	}

	@Override
	public String getFile() {
		// TODO Auto-generated method stub
		return file;
	}

	public static class DefaultMessageBuilder implements MessageBuilder {
		Throwable throwable;
		String text;
		Level level;
		int line;
		int column;
		Date timeStamp;
		int sequence;
		String jobId;
		String file;


		@Override
		public MessageBuilder withThrowable(Throwable throwable) {
			this.throwable = throwable;
			return this;
		}


		@Override
		public MessageBuilder withText(String text) {
			this.text = text;
			return this;
		}


		@Override
		public MessageBuilder withLevel(Level level) {
			this.level = level;
			return this;
		}


		@Override
		public MessageBuilder withLine(int line) {
			this.line = line;
			return this;
		}


		@Override
		public MessageBuilder withColumn(int column) {
			this.column = column;
			return this;
		}


		@Override
		public MessageBuilder withTimeStamp(Date timeStamp) {
			this.timeStamp = timeStamp;
			return this;
		}


		@Override
		public MessageBuilder withSequence(int sequence) {
			this.sequence = sequence;
			return this;
		}


		@Override
		public MessageBuilder withJobId(String jobId) {
			this.jobId = jobId;
			return this;
		}


		@Override
		public MessageBuilder withFile(String file) {
			this.file = file;
			return this;
		}

		@Override
		public Message build() {
			return new DefaultMessage(throwable, text, level, line, column,
					timeStamp, sequence, jobId, file);
		}

	}


}
