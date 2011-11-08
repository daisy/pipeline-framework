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

// TODO: Auto-generated Javadoc
/**
 * The Class PipelineWebService.
 */
public class PipelineWebService extends Application {
	
	/** The logger. */
	private static Logger logger = LoggerFactory
			.getLogger(PipelineWebService.class.getName());
	
	/** The Constant MODE_PROPERTY. */
	public static final String MODE_PROPERTY = "org.daisy.pipeline.mode";
	
	/** The Constant WS. */
	private static final String WS = "ws";

	// TODO make port and address configurable
	/** The server address. */
	private final String serverAddress = "http://localhost:8182/ws";
	
	/** The port number. */
	private final int portNumber = 8182;
	private final boolean usesAuthentication = false;
	
	/** The job manager. */
	private JobManager jobManager;
	
	/** The script registry. */
	private ScriptRegistry scriptRegistry;

	/* (non-Javadoc)
	 * @see org.restlet.Application#createInboundRoot()
	 */
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

	/**
	 * Inits the.
	 */
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

	/**
	 * Close.
	 */
	public void close() {
		logger.info("Webservice stopped.");
	}

	/**
	 * Gets the server address.
	 *
	 * @return the server address
	 */
	public String getServerAddress() {
		return this.serverAddress;
	}

	public boolean isAuthenticationEnabled() {
		return this.usesAuthentication;
	}
	
	/**
	 * Gets the job manager.
	 *
	 * @return the job manager
	 */
	public JobManager getJobManager() {
		return jobManager;
	}

	/**
	 * Sets the job manager.
	 *
	 * @param jobManager the new job manager
	 */
	public void setJobManager(JobManager jobManager) {
		this.jobManager = jobManager;
	}

	/**
	 * Gets the script registry.
	 *
	 * @return the script registry
	 */
	public ScriptRegistry getScriptRegistry() {
		return scriptRegistry;
	}

	/**
	 * Sets the script registry.
	 *
	 * @param scriptRegistry the new script registry
	 */
	public void setScriptRegistry(ScriptRegistry scriptRegistry) {
		this.scriptRegistry = scriptRegistry;
	}

}
