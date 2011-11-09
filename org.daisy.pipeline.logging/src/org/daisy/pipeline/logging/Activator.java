package org.daisy.pipeline.logging;

import java.util.logging.Handler;
import java.util.logging.LogManager;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * OSGI logging configuration class. Activates the standard java logging bridge for SLF4J.
 */
public class Activator implements BundleActivator {


	private static final Logger mLogger = LoggerFactory
			.getLogger(Activator.class);

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		mLogger.debug("earlyStartup slf4j SLF4JBridgeHandler...");
		java.util.logging.Logger rootLogger = LogManager.getLogManager()
				.getLogger("");
		Handler[] handlers = rootLogger.getHandlers();
		for (int i = 0; i < handlers.length; i++) {
			rootLogger.removeHandler(handlers[i]);
		}
		org.slf4j.bridge.SLF4JBridgeHandler.install();
	}

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
	}

}
