package org.daisy.pipeline.jobmanager;

import java.net.URI;
import java.util.LinkedList;

public class JobStatus {
	public enum Status{
		PROCESSING , COMPLETED , FAILED , NOT_STARTED
	}
	protected LinkedList<Error>mErrors=new LinkedList<JobStatus.Error>();
	protected URI mLog;
	protected Result mResult;
	protected Status mStatus;
	protected JobID mJobID;
	
	public Iterable<Error> getErrors() {
		return mErrors;
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


	static class Result{
		private URI mUri;
		private String mType;
		
		
		public Result(URI uri, String type) {
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
	
	
	static class Error{
		enum Level{
			WARNING, FATAL , ERROR
		}
		private Level mLevel;
		private String mDescription;
		
		
		public Error(Level level, String description) {
			super();
			mLevel = level;
			mDescription = description;
		}
		public Level getLevel() {
			return mLevel;
		}
		public void setLevel(Level level) {
			mLevel = level;
		}
		public String getDescription() {
			return mDescription;
		}
		public void setDescription(String description) {
			mDescription = description;
		}
		
		
		
	}
}
