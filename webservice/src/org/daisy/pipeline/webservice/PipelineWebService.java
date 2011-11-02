package org.daisy.pipeline.webservice;

import org.daisy.pipeline.job.JobManager;
import org.daisy.pipeline.script.ScriptRegistry;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PipelineWebService extends Application {
	private static Logger logger = LoggerFactory
			.getLogger(PipelineWebService.class.getName());
	public static final String MODE_PROPERTY = "org.daisy.pipeline.mode";
	private static final String WS = "ws";
	

	// TODO make port, address, and authentication on/off configurable
	private final String serverAddress = "http://localhost:8182/ws";
	private final int portNumber = 8182;
	private final boolean usesAuthentication = false;
	
	private JobManager jobManager;
	private ScriptRegistry scriptRegistry;

	@Override
	public Restlet createInboundRoot() {
		Router router = new Router(getContext());
		router.attach("/scripts", ScriptsResource.class);
		router.attach("/script", ScriptResource.class);
		router.attach("/jobs", JobsResource.class);
		router.attach("/jobs/{id}", JobResource.class);
		router.attach("/jobs/{id}/log", LogResource.class);
		router.attach("/jobs/{id}/result", ResultResource.class);
		return router;
	}

	public void init() {
		if (System.getProperty(MODE_PROPERTY) != null
				&& System.getProperty(MODE_PROPERTY).equalsIgnoreCase(WS)) {
			logger.info("Starting webservice on port 8182.");
			Component component = new Component();
			component.getServers().add(Protocol.HTTP, portNumber);
			component.getDefaultHost().attach("/ws", this);
			try {
				component.start();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void close() {
		logger.info("Webservice stopped.");
	}

	public String getServerAddress() {
		return this.serverAddress;
	}

	public boolean isAuthenticationEnabled() {
		return this.usesAuthentication;
	}
	
	public JobManager getJobManager() {
		return jobManager;
	}

	public void setJobManager(JobManager jobManager) {
		this.jobManager = jobManager;
	}

	public ScriptRegistry getScriptRegistry() {
		return scriptRegistry;
	}

	public void setScriptRegistry(ScriptRegistry scriptRegistry) {
		this.scriptRegistry = scriptRegistry;
	}

}
