package org.daisy.pipeline.logging.impl;

import java.util.logging.Handler;
import java.util.logging.LogManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JulToSlf4jBridgeSetup {
	
	private static final Logger mLogger = LoggerFactory
			.getLogger(Activator.class);
	
	public static void setup() {
		mLogger.debug("earlyStartup slf4j SLF4JBridgeHandler...");
		java.util.logging.Logger rootLogger = LogManager.getLogManager()
				.getLogger("");
		Handler[] handlers = rootLogger.getHandlers();
		for (Handler handler : handlers) {
			rootLogger.removeHandler(handler);
		}
		org.slf4j.bridge.SLF4JBridgeHandler.install();
	}
}
