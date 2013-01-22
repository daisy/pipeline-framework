package org.daisy.pipeline.job;

import java.io.IOException;

import java.net.URI;
import java.util.Set;

import org.daisy.common.xproc.XProcInput;

import org.daisy.pipeline.script.XProcScript;

class MappingJobContext extends AbstractJobContext {

	URITranslator translator;
	public MappingJobContext(JobId id, XProcInput input,XProcScript script,ResourceCollection collection) {
		super(id, input, script);
		try{
			translator=MappingURITranslator.from(id,script,collection);
			setInput(translator.translateInputs(input));

		}catch(IOException ex){
			throw new RuntimeException("Error while initialising the context",ex);
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
