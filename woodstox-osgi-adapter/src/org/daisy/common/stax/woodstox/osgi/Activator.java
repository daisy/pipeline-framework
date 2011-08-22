package org.daisy.common.stax.woodstox.osgi;
import javax.xml.stream.XMLInputFactory;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

	private ServiceRegistration registration;

	@Override
	public void start(final BundleContext context) throws Exception {
		registration = context.registerService(XMLInputFactory.class.getName(),
				new StaxInputFactoryServiceFactory(), null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		registration.unregister();
	}

}
