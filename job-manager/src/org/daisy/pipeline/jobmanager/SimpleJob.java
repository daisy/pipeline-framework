package org.daisy.pipeline.jobmanager;


public class SimpleJob extends Job{
	Runnable mRunnable;
	protected static IDFactory mIDFactory= new StringJobID.StringIDFactory();
	public SimpleJob(JobID newId,Runnable runnable) {
		super.mId=newId;
		super.mStatus=new MutableJobStatus();
		mRunnable=runnable;
	}

	public void setID(JobID id){
		super.mId=id;
		
	}

	@Override
	IDFactory getIDFactory() {
		return mIDFactory;
	}

	@Override
	Runnable getRunnable() {
		return mRunnable;
	}
}
