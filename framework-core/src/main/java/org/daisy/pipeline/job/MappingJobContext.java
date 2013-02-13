package org.daisy.pipeline.job;

import java.io.IOException;

import org.daisy.pipeline.script.BoundXProcScript;

final class MappingJobContext extends AbstractJobContext {

	public MappingJobContext(JobId id, BoundXProcScript boundScript,ResourceCollection collection) throws IOException{
		super(id, boundScript,JobURIUtils.newURIMapper(id));
		XProcDecorator decorator=XProcDecorator.from(this.getScript(),this.getMapper(),collection);
		setInput(decorator.decorate(this.getInputs()));
		setOutput(decorator.decorate(this.getOutputs()));

	}

}
