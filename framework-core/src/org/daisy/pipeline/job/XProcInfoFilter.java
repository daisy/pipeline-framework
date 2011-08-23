package org.daisy.pipeline.job;

import org.daisy.common.xproc.XProcPipelineInfo;
import org.daisy.pipeline.script.XProcScript;

public class XProcInfoFilter implements Filter<XProcPipelineInfo> {

	public static final XProcInfoFilter INSTANCE = new XProcInfoFilter();

	@Override
	public XProcPipelineInfo filter(XProcPipelineInfo in) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public XProcScript filterScript(XProcScript script) {
		// TODO write filter
		return null;
	}

}
