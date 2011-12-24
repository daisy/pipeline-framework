package org.daisy.common.xproc;

import org.daisy.common.messaging.MessageAccessor;



public interface XProcMonitor{
	public void setMessageAccessor(MessageAccessor accessor);
	public MessageAccessor getMessageAccessor();
	//TODO: add profiling accessors
}
