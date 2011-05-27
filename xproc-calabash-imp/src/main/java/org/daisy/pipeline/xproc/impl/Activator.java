package org.daisy.pipeline.xproc.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		org.slf4j.impl.OSGILogFactory.initOSGI(context); 
		Logger logger = LoggerFactory.getLogger(Activator.class);
		logger.debug("Hello log");
		
	}

	@Override
	public void stop(BundleContext arg0) throws Exception {
		// TODO Auto-generated method stub

	}

}