package org.daisy.pipeline.persistence;


import org.daisy.common.messaging.MessageAccessor;
import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.job.JobMonitor;
import org.daisy.pipeline.persistence.messaging.PersistentMessageAccessor;

public class PersistentJobMonitor implements JobMonitor{
	PersistentMessageAccessor accessor;
	public PersistentJobMonitor(JobId id) {
		accessor=new PersistentMessageAccessor(id);		
	}
	@Override
	public MessageAccessor getMessageAccessor() {
		return accessor;
	}
	
	
}
