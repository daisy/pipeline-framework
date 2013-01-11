package org.daisy.pipeline.job;

import org.daisy.common.xproc.XProcInput;

import org.daisy.pipeline.script.XProcScript;

class LocalJobContext extends JobContext{

	public LocalJobContext(JobId id, XProcInput input,XProcScript script) {
		super(id, input, script);
	}


}
