package org.daisy.common.xproc;


public interface XProcPipeline {
	XProcPipelineInfo getInfo();
	XProcResult run(XProcInput data);
}
