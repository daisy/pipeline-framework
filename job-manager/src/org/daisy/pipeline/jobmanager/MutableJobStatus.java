package org.daisy.pipeline.jobmanager;

import java.net.URI;

class MutableJobStatus extends JobStatus{

	public void addError(Error err){
		super.mErrors.add(err);
	}
	
	public void setStatus(Status status){
		super.mStatus=status;
	}
	
	public void setResult(Result res){
		super.mResult=res;
	}
	public void setLog(URI log){
		super.mLog=log;
	}
	
	
}
