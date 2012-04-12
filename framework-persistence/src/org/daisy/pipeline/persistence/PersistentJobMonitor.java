package org.daisy.pipeline.persistence;


import org.daisy.common.messaging.MessageAccessor;
import org.daisy.common.messaging.MessageListener;
import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.job.JobMonitor;
import org.daisy.pipeline.persistence.messaging.PersistentMessageListener;

public class PersistentJobMonitor implements JobMonitor{
	PersistentMessageListener mListener;
	public PersistentJobMonitor(JobId id) {
		mListener=new PersistentMessageListener(id);		
	}
	@Override
	public MessageAccessor getMessageAccessor() {

		return mListener;
	}
	@Override
	public MessageListener getMessageListener() {
		return mListener;
	}
	
}
