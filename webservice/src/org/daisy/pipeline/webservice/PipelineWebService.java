package org.daisy.pipeline.webservice;

import java.io.File;

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
	
	/* other runtime-configurable property names */
	public static final String PORT_PROPERTY = "org.daisy.pipeline.ws.port";
	public static final String HOST_PROPERTY = "org.daisy.pipeline.ws.host";
	public static final String PATH_PROPERTY = "org.daisy.pipeline.ws.path";
	public static final String MAX_REQUEST_TIME_PROPERTY = "org.daisy.pipeline.ws.maxrequesttime";
	public static final String TMPDIR_PROPERTY = "org.daisy.pipeline.ws.tmpdir";
	public static final String AUTHENTICATION_PROPERTY = "org.daisy.pipeline.ws.authentication";
	public static final String DBPATH_PROPERTY = "org.daisy.pipeline.ws.dbpath";
	
	/* options and their default values */
	private String host = "localhost";
	private String path = "/ws";
	private int portNumber = 8182;
	private boolean usesAuthentication = true;
	private long maxRequestTime = 600000; // 10 minutes in ms
	private String tmpDir = "/tmp";
	private String dbPath = "";
	
	/** The Constant WS. */
	private static final String WS = "ws";
	
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
	 * Inits the WS.
	 */
	public void init() {
		readOptions();
		if (System.getProperty(MODE_PROPERTY) != null && System.getProperty(MODE_PROPERTY).equalsIgnoreCase(WS)) {
			logger.info(String.format("Starting webservice on port %d", this.portNumber));
			Component component = new Component();
			component.getServers().add(Protocol.HTTP, portNumber);
			component.getDefaultHost().attach(path, this);
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
		// format as http://hostname:port/path
		String serverAddress = String.format("http://%s:%d%s", this.host, this.portNumber, this.path);
		return serverAddress;
	}

	public String getTmpDir() {
		return this.tmpDir;
	}
	
	public boolean isAuthenticationEnabled() {
		return this.usesAuthentication;
	}
	
	public int getPortNumber() {
		return this.portNumber;
	}
	
	// the length of time in ms that a request is valid for, counting from its timestamp value
	public long getMaxRequestTime() {
		return this.maxRequestTime;
	}
	
	public String getDBPath() {
		return this.dbPath;
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
	
	private void readOptions() {
		// make sure we're in WS mode; else don't bother reading options
		if (System.getProperty(MODE_PROPERTY) == null || 
				!System.getProperty(MODE_PROPERTY).equalsIgnoreCase(WS)) {
			return;
		}
		
		String host = System.getProperty(HOST_PROPERTY);
		if (host != null) {
			this.host = host;
		}
		
		String path = System.getProperty(PATH_PROPERTY);
		if (path != null) {
			if (!path.startsWith("/")) {
				path = "/" + path;
			}
			this.path = path;
		}
		
		String authentication = System.getProperty(AUTHENTICATION_PROPERTY);
		
		// TODO remove this line; it's is for TESTING ONLY
		this.usesAuthentication = false;
		/*
		if (authentication != null) {
			if (authentication.equalsIgnoreCase("true")) {
				this.usesAuthentication = true;
			}
			else if (authentication.equalsIgnoreCase("false")) {
				this.usesAuthentication = false;
				logger.info("Web service authentication is OFF");
			}
			else {
				logger.error(String.format(
						"Value specified in option %s (%s) is not valid. Using default value of %s.", 
						AUTHENTICATION_PROPERTY, authentication, this.usesAuthentication));
			}
		}*/
		
		String port = System.getProperty(PORT_PROPERTY);
		if (port != null) {
			try {
				int portnum = Integer.parseInt(port);
				if (portnum >= 0 && portnum <= 65535) { 
					this.portNumber = portnum;
				}
				else {
					logger.error(String.format(
							"Value specified in option %s (%d) is not valid. Using default value of %d.", 
							PORT_PROPERTY, portnum, this.portNumber));
				}
			} catch (NumberFormatException e) {
				logger.error(String.format(
						"Value specified in option %s (%s) is not a valid numeric value. Using default value of %d.", 
						PORT_PROPERTY, port, this.portNumber));
			}
		}
		
		String tmp = System.getProperty(TMPDIR_PROPERTY);
		if (tmp != null) {
			File f = new File(tmp);
			if (f.exists()) {
				this.tmpDir = tmp;
			}
			else {
				logger.error(String.format(
						"Value specified in option %s (%s) is not valid. Using default value of %s.", 
						TMPDIR_PROPERTY, tmp, this.tmpDir));
			}
		}
		
		String maxrequesttime = System.getProperty(MAX_REQUEST_TIME_PROPERTY);
		if (maxrequesttime != null) {
			try {
				long ms = Long.parseLong(maxrequesttime);
				this.maxRequestTime = ms;
			} catch(NumberFormatException e) {
				logger.error(String.format(
						"Value specified in option %s (%s) is not a valid numeric value. Using default value of %d.", 
						MAX_REQUEST_TIME_PROPERTY, maxrequesttime, this.maxRequestTime));
			}
		}
		
		String dbpath = System.getProperty(DBPATH_PROPERTY);
		if (dbpath != null) {
			this.dbPath = dbpath;
		}
	}
}
