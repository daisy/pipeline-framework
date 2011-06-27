package org.daisy.pipeline.jobmanager;

public interface IDFactory {
	public JobID getNewID(String prefix);
	public JobID fromString(String str);
}
