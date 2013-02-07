package org.daisy.pipeline.job;

import java.io.IOException;


import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcOutput;
import org.daisy.common.xproc.XProcResult;

import org.daisy.pipeline.script.XProcScript;

final class MappingJobContext extends AbstractJobContext {

	public MappingJobContext(JobId id, XProcScript script,XProcInput input,XProcOutput output,ResourceCollection collection) throws IOException{
		super(id, script,input,output,JobURIUtils.newURIMapper(id));
		XProcDecorator decorator=XProcDecorator.from(script,this.getMapper(),collection);
		setInput(decorator.decorate(input));
		setOutput(decorator.decorate(output));

	}

}
