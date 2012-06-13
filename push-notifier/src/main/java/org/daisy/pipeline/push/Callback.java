package org.daisy.pipeline.push;

import java.net.URI;

import org.daisy.pipeline.job.JobId;

public class Callback {
	public enum CallbackType {STATUS, MESSAGES}

	private final URI href;
	private final CallbackType type;
	private final JobId jobId;
	private int frequency = 1;

	public Callback(JobId jobId, URI href, CallbackType type) {
		this.href = href;
		this.type = type;
		this.jobId = jobId;
	}

	public Callback(JobId jobId, URI href, CallbackType type, int frequency) {
		this.href = href;
		this.type = type;
		this.jobId = jobId;
		this.frequency = frequency;
	}
	public JobId getJobId() {
		return jobId;
	}

	public URI getHref() {
		return href;
	}

	public CallbackType getType() {
		return type;
	}

	public int getFrequency() {
		return frequency;
	}
}