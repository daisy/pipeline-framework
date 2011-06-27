package org.daisy.pipeline.jobmanager;


public class StringJobID implements JobID{
	private String mId;

	@Override
	public int compareTo(JobID other) {
		
		return mId.compareTo(other.getID());
			
	}
	
	
	@Override
	public int hashCode() {
		return mId.hashCode();
	}

	
	@Override
	public boolean equals(Object other) {
		return mId.equals(other.toString());
	}


	protected StringJobID(String id) {
		super();
		mId = id;
	}

	public String getID(){
		return mId;
	}
	@Override
	public String toString() {
	
		return mId;
	}
	
	
	public static class StringIDFactory implements IDFactory{
		private static int mCount=0; 
		@Override
		public JobID getNewID(String prefix) {
			return new StringJobID(prefix+"#"+mCount++);
		}
		@Override
		public JobID fromString(String str) {
			return new StringJobID(str);
		}
	
	} 
	
}
