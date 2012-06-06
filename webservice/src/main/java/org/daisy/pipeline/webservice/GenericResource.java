package org.daisy.pipeline.webservice;

import org.restlet.resource.ServerResource;

public abstract class GenericResource extends ServerResource {

	protected PipelineWebService webservice() {
		return (PipelineWebService) getApplication();
	}

}