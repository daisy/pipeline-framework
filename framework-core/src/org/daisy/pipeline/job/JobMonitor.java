package org.daisy.pipeline.job;

import org.daisy.common.messaging.MemoryMessageListener;
import org.daisy.common.messaging.MessageAccessor;
import org.daisy.common.messaging.MessageListener;
import org.daisy.common.xproc.XProcMonitor;


public class JobMonitor implements XProcMonitor{
	MemoryMessageListener mListener=new MemoryMessageListener();

	@Override
	public MessageAccessor getMessageAccessor() {
		return mListener;
	}
	@Override
	public MessageListener getMessageListener() {
		return mListener;
	}

	public static class DefaultJobMonitorFactory implements JobMonitorFactory{

		@Override
		public JobMonitor newJobMonitor(JobId id) {

			return new JobMonitor();
		}

	}

}
