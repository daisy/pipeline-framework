package org.daisy.pipeline.job;

public interface JobIdGenerator {
	public JobId generateId();
	public JobId generateIdFromString(String base);
	public interface JobId extends Comparable<JobId>{}
}
