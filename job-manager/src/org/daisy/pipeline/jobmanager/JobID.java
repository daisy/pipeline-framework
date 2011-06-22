package org.daisy.pipeline.jobmanager;

public class JobID implements Comparable<JobID>{
	private String  mId;
	@Override
	public int compareTo(JobID other) {

		return 0;
	}
	
	public JobID(String id) {
		super();
		mId = id;
	}

	public String getID(){
		return mId;
	}
	
	

}
