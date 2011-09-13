package org.daisy.common.xproc;
import org.daisy.common.messaging.MessageAccessor;



public interface XProcResult {
	
	void writeTo(XProcOutput output);
	MessageAccessor getMessages();
}
