package org.daisy.common.xproc;

import org.daisy.common.messaging.MessageAccessor;
import org.daisy.common.messaging.MessageListener;



public interface XProcMonitor {

	public MessageAccessor getMessageAccessor();
	public MessageListener getMessageListener();
	//TODO: add profiling accessors
}
