package org.daisy.pipeline.webservice;

import org.daisy.pipeline.DaisyPipelineContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.restlet.Component;
import org.restlet.data.Protocol;

public class Activator implements BundleActivator {

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		System.out.println("Starting webservice on port 8182.");
		
		ServiceReference sr = context.getServiceReference(org.daisy.pipeline.DaisyPipelineContext.class.getName());
		DaisyPipelineContext daisyPipelineContext = (DaisyPipelineContext)context.getService(sr);
		
		Component component = new Component();
		component.getServers().add(Protocol.HTTP, 8182);
		WebApplication application = new WebApplication();
		application.setDaisyPipelineContext(daisyPipelineContext);
	
		component.getDefaultHost().attach("/ws", application);
		
		// TODO: how to get this information dynamically?
		application.setServerAddress("http://localhost:8182/ws");
		
		component.start();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		System.out.println("Webservice stopped.");
	}

}
