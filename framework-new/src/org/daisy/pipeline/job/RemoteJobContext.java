package org.daisy.pipeline.job;

import java.io.File;

import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcOutput;

public class RemoteJobContext extends LocalJobContext {

	private File dataDir;

	public static RemoteJobContext newContext(XProcInput input,XProcOutput output) {
		//	initialize the data directory
		throw new UnsupportedOperationException();
	}


	@Override
	public XProcInput getInput() {
		// TODO translate the XProcInput
		return null;
	}

	@Override
	public XProcOutput getOutput(){
		// TODO translate the XProcInput
		return null;
	}

}
