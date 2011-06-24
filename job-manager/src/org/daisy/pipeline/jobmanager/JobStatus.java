package org.daisy.pipeline.jobmanager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;

public class JobStatus {
	public enum Status{
		PROCESSING , COMPLETED , FAILED , NOT_STARTED
	}
	protected LinkedList<JobError>mErrors=new LinkedList<JobError>();
	protected URI mLog;
	protected JobResult mResult;
	protected Status mStatus;
	protected JobID mJobID;
	
	
	public JobStatus() {
		mStatus=Status.NOT_STARTED;
		try {
			mLog=new URI("file:/pathtolog");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public Iterable<Error> getErrors() {
		return new LinkedList<Error>(mErrors); 
	}


	public URI getLog() {
		return mLog;
	}


	public Result getResult() {
		return mResult;
	}


	public Status getStatus() {
		return mStatus;
	}


	public JobID getJobID() {
		return mJobID;
	}


	static class JobResult implements Result{
		private URI mUri;
		private String mType;
		
		
		public JobResult(URI uri, String type) {
			super();
			mUri = uri;
			mType = type;
		}
		public URI getUri() {
			return mUri;
		}
		public void setUri(URI uri) {
			mUri = uri;
		}
		public String getType() {
			return mType;
		}
		public void setType(String type) {
			mType = type;
		}
		
		
	}
	
	
	static class JobError implements Error{
		
		private Level mLevel;
		private String mDescription;
		
		
		public JobError(Level level, String description) {
			super();
			mLevel = level;
			mDescription = description;
		}
		public Level getLevel() {
			return mLevel;
		}
		/* (non-Javadoc)
		 * @see org.daisy.pipeline.jobmanager.Error#setLevel(org.daisy.pipeline.jobmanager.JobStatus.JobError.Level)
		 */
		@Override
		public void setLevel(Level level) {
			mLevel = level;
		}
		public String getDescription() {
			return mDescription;
		}
		/* (non-Javadoc)
		 * @see org.daisy.pipeline.jobmanager.Error#setDescription(java.lang.String)
		 */
		@Override
		public void setDescription(String description) {
			mDescription = description;
		}
		
		
		
	}
}
