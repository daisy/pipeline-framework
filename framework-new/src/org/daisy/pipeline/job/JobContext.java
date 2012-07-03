package org.daisy.pipeline.job;

import java.net.URI;

import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcOutput;

public interface JobContext {
	//local returns, remote translates
	XProcInput getInput();

	XProcOutput getOutput();

	URI getLogURI();
}
