package org.daisy.pipeline.job;

import java.net.URI;
import java.util.Set;

import org.daisy.common.xproc.XProcInput;

import org.daisy.pipeline.script.XProcScript;

public class SimpleJobContext extends AbstractJobContext{

	public SimpleJobContext(JobId id, XProcInput input,XProcScript script) {
		super(id, input, script);
	}

	@Override
	public void writeXProcResult() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<URI> getFiles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URI getZip() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URI toZip(URI... files) {
		// TODO Auto-generated method stub
		return null;
	}


}
