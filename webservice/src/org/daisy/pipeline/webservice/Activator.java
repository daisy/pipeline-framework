package org.daisy.pipeline.webservice;

import org.daisy.pipeline.DaisyPipelineContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.restlet.Component;
import org.restlet.data.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator {

	private class PipelineContextTrackerCustomizer implements ServiceTrackerCustomizer{
		

		@Override
		public Object addingService(ServiceReference ref) {
			
				//mLogger.info("starting cmd");
			DaisyPipelineContext daisyPipelineContext = (DaisyPipelineContext) context.getService(ref);
			Component component = new Component();
			component.getServers().add(Protocol.HTTP, 8182);
			WebApplication application = new WebApplication();
			application.setDaisyPipelineContext(daisyPipelineContext);
			
			component.getDefaultHost().attach("/ws", application);
			
			// TODO: how to get this information dynamically?
			application.setServerAddress("http://localhost:8182/ws");
			
			try {
				component.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void modifiedService(ServiceReference arg0, Object arg1) {
		}

		@Override
		public void removedService(ServiceReference arg0, Object arg1) {
			//TODO stop the component
		}
		
	}
	private ServiceTracker pipelineContextTracker;
private BundleContext context;
public static final String WS = "ws";
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
    Logger mLogger = LoggerFactory.getLogger(getClass().getCanonicalName());
	public void start(BundleContext context) throws Exception {
		if (System.getProperty(DaisyPipelineContext.MODE_PROPERTY) != null
				&& System.getProperty(DaisyPipelineContext.MODE_PROPERTY)
						.equals(WS)) {
		this.context=context;
		mLogger.info("Starting webservice on port 8182.");
		
		pipelineContextTracker = new ServiceTracker(context, DaisyPipelineContext.class.getName(), new PipelineContextTrackerCustomizer());
		pipelineContextTracker.open();
		
		DaisyPipelineContext daisyPipelineContext = (DaisyPipelineContext) pipelineContextTracker.waitForService(5000);
//		ServiceReference sr = context.getServiceReference(org.daisy.pipeline.DaisyPipelineContext.class.getName());
//		DaisyPipelineContext daisyPipelineContext = (DaisyPipelineContext)context.getService(sr);
		}
		
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		pipelineContextTracker.close();
		mLogger.info("Webservice stopped.");
	}

}
