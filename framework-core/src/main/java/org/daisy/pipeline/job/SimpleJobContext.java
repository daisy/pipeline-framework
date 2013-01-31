package org.daisy.pipeline.job;

import java.net.URI;
import java.util.Set;

import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcOutput;

import org.daisy.pipeline.script.XProcScript;

final class SimpleJobContext extends AbstractJobContext{

	public SimpleJobContext(JobId id,XProcScript script,XProcInput input,XProcOutput output) {
		super(id, script,input,output);
		try{
			translator=SimpleURITranslator.from(script);
			setInput(translator.translateInputs(input));
			setOutput(translator.translateOutput(output));

		}catch(Exception ex){
			throw new RuntimeException("Error while initialising the mapping context",ex);
		}
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
