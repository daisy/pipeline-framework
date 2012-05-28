package org.daisy.pipeline.persistence.messaging;


public class PersistenceMessagePK {

	int sequence;
	String jobId;
	
	public PersistenceMessagePK(int sequence, String jobId) {
		super();
		this.sequence = sequence;
		this.jobId = jobId;
	}
	
}
