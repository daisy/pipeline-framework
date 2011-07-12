package org.daisy.pipeline.webservice;

import org.daisy.pipeline.DaisyPipelineContext;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class WebApplication extends Application {
	private DaisyPipelineContext daisyPipelineContext;
	// the base address of the server, such as http://localhost:8182/ws
	private String serverAddress;
	
	public void setDaisyPipelineContext (DaisyPipelineContext daisyPipelineContext) {
		this.daisyPipelineContext = daisyPipelineContext;
	}
	
	public DaisyPipelineContext getDaisyPipelineContext() {
		return daisyPipelineContext;
	}
	
	@Override
	public Restlet createInboundRoot() {
		Router router = new Router(getContext());
		router.attach("/converters", ConvertersResource.class);
		//router.attach("/converters/{name}", ConverterResource.class);
		router.attach("/converter{?id,uri}", ConverterResource.class);
		router.attach("/jobs", JobsResource.class);
		router.attach("/jobs/{id}", JobResource.class);
		router.attach("/jobs/{id}/log", LogResource.class);
		router.attach("/jobs/{id}/result", ResultResource.class);
		return router;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}
	public String getServerAddress() {
		return this.serverAddress;
	}
}
