package org.daisy.pipeline.job.impl;


import org.daisy.pipeline.clients.Client;
import org.daisy.pipeline.job.AbstractJobContext;
import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.script.BoundXProcScript;

public final class SimpleJobContext extends AbstractJobContext{

	public SimpleJobContext(Client client,JobId id,String niceName,BoundXProcScript boundScript) {
		super(client,id,niceName, boundScript,JobURIUtils.newURIMapper());
		try{
			XProcDecorator decorator=XProcDecorator.from(this.getScript(),this.getMapper());
			this.setOutput(decorator.decorate(this.getOutputs()));
			this.generateResults=false;

		}catch(Exception ex){
			throw new RuntimeException("Error while initialising the mapping context",ex);
		}
	}

}
