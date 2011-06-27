package org.daisy.pipeline.jobmanager;


public interface Error {
	enum Level{
		WARNING, FATAL , ERROR
	}
	public abstract void setLevel(Level level);

	public abstract void setDescription(String description);

}