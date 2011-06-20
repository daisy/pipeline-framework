package org.daisy.calabash;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.LoggerFactory;


public class Activator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		org.slf4j.impl.OSGILogFactory.initOSGI(context); 

		LoggerFactory.getLogger(this.getClass()).info("calabash-extensions activated ");
	}

	public void stop(BundleContext arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
