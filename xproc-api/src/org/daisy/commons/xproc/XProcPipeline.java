package org.daisy.commons.xproc;


public interface XProcPipeline {
	XProcPipelineInfo getInfo();
	XProcResult run(XProcInput data);
}
