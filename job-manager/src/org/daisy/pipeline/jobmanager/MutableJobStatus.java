package org.daisy.pipeline.jobmanager;

import java.net.URI;

class MutableJobStatus extends JobStatus{

	public void addError(JobError err){
		super.mErrors.add(err);
	}
	public void setJobID(JobID jobId){
		mJobID=jobId;
	}
	public void setStatus(Status status){
		super.mStatus=status;
	}
	
	public void setResult(JobResult res){
		super.mResult=res;
	}
	public void setLog(URI log){
		super.mLog=log;
	}
	
	
}
