package org.daisy.pipeline.jobmanager;

public abstract class  Job {
	MutableJobStatus mStatus;
	JobID mId;
	
	public JobStatus getStatus(){
		return mStatus;
	}
	public JobID getId() {
		return mId;
	}
	
	MutableJobStatus getMutableStatus(){
		return mStatus;
	}
	abstract IDFactory getIDFactory();
	
	abstract Runnable getRunnable();
	
}
