package org.daisy.common.xproc;

import org.daisy.common.messaging.MessageAccessor;


public interface XProcPipeline {
	XProcPipelineInfo getInfo();
	XProcResult run(XProcInput data);
	MessageAccessor getMessages();
}
