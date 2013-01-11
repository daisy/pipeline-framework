package org.daisy.pipeline.job;

import org.daisy.common.xproc.XProcInput;

import org.daisy.pipeline.script.XProcScript;

class RemoteJobContext extends JobContext{

	public RemoteJobContext(JobId id, XProcInput input,XProcScript script) {
		super(id, input, script);
	}


}
