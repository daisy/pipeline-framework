package org.daisy.pipeline.job;


import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcOutput;

import org.daisy.pipeline.script.XProcScript;

final class SimpleJobContext extends AbstractJobContext{

	public SimpleJobContext(JobId id,XProcScript script,XProcInput input,XProcOutput output) {
		super(id, script,input,output,JobURIUtils.newURIMapper());
		try{
			XProcDecorator decorator=XProcDecorator.from(script,this.getMapper());
			setOutput(decorator.decorate(output));

		}catch(Exception ex){
			throw new RuntimeException("Error while initialising the mapping context",ex);
		}
	}

}
