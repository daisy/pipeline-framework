package org.daisy.pipeline.event;

/**
 * Event for notifying that a job message that has been previously put on the event bus
 * has been updated.
 */
public class MessageUpdate {

	private final String jobId;
	private final int sequence;

	/* Package private constructor */
	MessageUpdate(String jobId, int sequence) {
		this.jobId = jobId;
		this.sequence = sequence;
	}

	/** The message that has been updated */
	public String getJobId() {
		return jobId;
	}

	/** Sequence number that represents the update */
	public int getSequence() {
		return sequence;
	}

	@Override
	public String toString() {
		return sequence + " within " + jobId;
	}
}
