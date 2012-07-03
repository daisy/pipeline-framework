package org.daisy.pipeline.job;

import java.io.File;
import java.net.URI;

import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcOutput;

public class LocalJobContext implements JobContext {

	private File dataDir;

	public static LocalJobContext newContext(XProcInput input,
			XProcOutput output) {
		// initialize the data directory
		throw new UnsupportedOperationException();
	}

	@Override
	public XProcInput getInput() {
		// returns directly;
		return null;
	}

	@Override
	public XProcOutput getOutput() {
		// returns directly;
		return null;
	}

	@Override
	public URI getLogURI() {
		// TODO generate it from the data directory
		throw new UnsupportedOperationException();
	}

}
