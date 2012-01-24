package org.daisy.pipeline.webservice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import org.daisy.pipeline.job.JobManager;
import org.daisy.pipeline.script.ScriptRegistry;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
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
	private static Logger logger = LoggerFactory.getLogger(PipelineWebService.class.getName());
	
	/* other runtime-configurable property names */
	public static final String PORT_PROPERTY = "org.daisy.pipeline.ws.port";
	public static final String PATH_PROPERTY = "org.daisy.pipeline.ws.path";
	public static final String MAX_REQUEST_TIME_PROPERTY = "org.daisy.pipeline.ws.maxrequesttime";
	public static final String TMPDIR_PROPERTY = "org.daisy.pipeline.ws.tmpdir";
	public static final String AUTHENTICATION_PROPERTY = "org.daisy.pipeline.ws.authentication";
	public static final String LOCAL_MODE = "org.daisy.pipeline.ws.local";
	
	public static final String KEY_FILE_NAME="dp2key.txt";
	private static final String JAVA_IO_TMPDIR = "java.io.tmpdir";
	
	public static final String SCRIPTS_ROUTE = "/scripts";
	public static final String SCRIPT_ROUTE = "/scripts/{scriptid}"; 
	public static final String JOBS_ROUTE = "/jobs";
	public static final String JOB_ROUTE = "/jobs/{id}";
	public static final String LOG_ROUTE = "/jobs/{id}/log";
	public static final String RESULT_ROUTE = "/jobs/{id}/result";
	public static final String HALT_ROUTE = "/admin/halt/{key}";
	public static final String CLIENTS_ROUTE = "/admin/clients";
	public static final String CLIENT_ROUTE = "/admin/clients/{id}";
	
	
	/* options and their default values */
	private String path = "/ws";
	private int portNumber = 8182;
	private boolean usesAuthentication = true;
	private long maxRequestTime = 600000; // 10 minutes in ms
	private String tmpDir = "/tmp";
	
	/** The Constant WS. */
	//private static final String WS = "ws";
	
	/** The job manager. */
	private JobManager jobManager;
	
	/** The script registry. */
	private ScriptRegistry scriptRegistry;
	
	private long shutDownKey=0L;

	private BundleContext bundleCtxt; 
	/* (non-Javadoc)
	 * @see org.restlet.Application#createInboundRoot()
	 */
	@Override
	public Restlet createInboundRoot() {
		Router router = new Router(getContext());
		router.attach(SCRIPTS_ROUTE, ScriptsResource.class);
		router.attach(SCRIPT_ROUTE, ScriptResource.class);
		router.attach(JOBS_ROUTE, JobsResource.class);
		router.attach(JOB_ROUTE, JobResource.class);
		router.attach(LOG_ROUTE, LogResource.class);
		router.attach(RESULT_ROUTE, ResultResource.class);
		
		
		// init the administrative paths
		router.attach(CLIENTS_ROUTE, ClientsResource.class);
		router.attach(CLIENT_ROUTE, ClientResource.class);
		router.attach(HALT_ROUTE, HaltResource.class);
		
		return router;
	}

	/**
	 * Inits the WS.
	 */
	public void init(BundleContext ctxt) {
		bundleCtxt=ctxt;
		readOptions();
		logger.info(String.format("Starting webservice on port %d",
				this.portNumber));
		Component component = new Component();
		component.getServers().add(Protocol.HTTP, portNumber);
		component.getDefaultHost().attach(path, this);
		try {
			component.start();
			this.generateStopKey();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void generateStopKey() throws IOException {
		shutDownKey = new Random().nextLong();
		File fout = new File(System.getProperty(JAVA_IO_TMPDIR)+File.separator+KEY_FILE_NAME);
		FileOutputStream fos= new FileOutputStream(fout);
		fos.write((shutDownKey+"").getBytes());
		fos.close();
		logger.info("Shutdown key stored to: "+System.getProperty(JAVA_IO_TMPDIR)+File.separator+KEY_FILE_NAME);
	}
	
	public boolean shutDown(long key) throws BundleException{
		if(key==shutDownKey){
			//framework bundle id == 0
			((Framework)bundleCtxt.getBundle(0)).stop();
			return true;
		}
		return false;
		
	}

	/**
	 * Close.
	 * @throws Exception 
	 * @throws Throwable 
	 */
	public void close() throws Exception {
		logger.info("Webservice stopped.");
		this.stop();
		
	}


	public boolean isLocal() {
		return Boolean.valueOf(System.getProperty(LOCAL_MODE));
	}
	
	public String getTmpDir() {
		return this.tmpDir;
	}
	
	public boolean isAuthenticationEnabled() {
		return this.usesAuthentication;
	}
	
	// the length of time in ms that a request is valid for, counting from its timestamp value
	public long getMaxRequestTime() {
		return this.maxRequestTime;
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
		
		String path = System.getProperty(PATH_PROPERTY);
		if (path != null) {
			if (!path.startsWith("/")) {
				path = "/" + path;
			}
			this.path = path;
		}
		
		String authentication = System.getProperty(AUTHENTICATION_PROPERTY);
		
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
		}
		
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
	}
}
