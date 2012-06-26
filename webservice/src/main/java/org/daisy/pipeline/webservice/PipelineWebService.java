package org.daisy.pipeline.webservice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import org.daisy.pipeline.job.JobManager;
import org.daisy.pipeline.script.ScriptRegistry;
import org.daisy.pipeline.webserviceutils.Routes;
import org.daisy.pipeline.webserviceutils.callback.CallbackRegistry;
import org.daisy.pipeline.webserviceutils.clients.Client;
import org.daisy.pipeline.webserviceutils.clients.ClientStore;
import org.daisy.pipeline.webserviceutils.clients.SimpleClient;
import org.daisy.pipeline.webserviceutils.requestlog.RequestLog;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.Server;
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
	public static final String MAX_REQUEST_TIME_PROPERTY = "org.daisy.pipeline.ws.maxrequesttime";
	public static final String TMPDIR_PROPERTY = "org.daisy.pipeline.ws.tmpdir";
	public static final String AUTHENTICATION_PROPERTY = "org.daisy.pipeline.ws.authentication";
	public static final String LOCAL_MODE_PROPERTY = "org.daisy.pipeline.ws.local";
	public static final String JAVA_IO_TMPDIR_PROPERTY = "java.io.tmpdir";
	
	public static final String KEY_FILE_NAME="dp2key.txt";

	private boolean usesAuthentication = true;
	private long maxRequestTime = 600000; // 10 minutes in ms
	private String tmpDir = "/tmp";
	
	/** The job manager. */
	private JobManager jobManager;

	/** The script registry. */
	private ScriptRegistry scriptRegistry;

    /** The Client Store **/
	private ClientStore<?> clientStore;

	private CallbackRegistry callbackRegistry;

	private long shutDownKey=0L;

	private BundleContext bundleCtxt;

	private RequestLog requestLog;
	/* (non-Javadoc)
	 * @see org.restlet.Application#createInboundRoot()
	 */
	@Override
	public Restlet createInboundRoot() {
		Router router = new Router(getContext());
		router.attach(Routes.SCRIPTS_ROUTE, ScriptsResource.class);
		router.attach(Routes.SCRIPT_ROUTE, ScriptResource.class);
		router.attach(Routes.JOBS_ROUTE, JobsResource.class);
		router.attach(Routes.JOB_ROUTE, JobResource.class);
		router.attach(Routes.LOG_ROUTE, LogResource.class);
		router.attach(Routes.RESULT_ROUTE, ResultResource.class);

		// init the administrative paths
		router.attach(Routes.CLIENTS_ROUTE, ClientsResource.class);
		router.attach(Routes.CLIENT_ROUTE, ClientResource.class);
		router.attach(Routes.HALT_ROUTE, HaltResource.class);

		return router;
	}

	/**
	 * Inits the WS.
	 */
	public void init(BundleContext ctxt) {
		bundleCtxt=ctxt;
		readOptions();
		Routes routes = new Routes();
		
		logger.info(String.format("Starting webservice on port %d",
				routes.getPort()));
		Component component = new Component();
		Server theServer = component.getServers().add(Protocol.HTTP, routes.getPort());
		component.getDefaultHost().attach(routes.getPath(), this);
		this.setStatusService(new PipelineStatusService());
		try {
			component.start();
			generateStopKey();
		} catch (Exception e) {
			logger.error("Shutting down the framework because of:"+e.getMessage());
			try{
				((Framework)bundleCtxt.getBundle(0)).stop();
			}catch (Exception innerException) {
				logger.error("Error shutting down:"+e.getMessage());
			}

		}
	}

	private void generateStopKey() throws IOException {
		shutDownKey = new Random().nextLong();
		File fout = new File(System.getProperty(JAVA_IO_TMPDIR_PROPERTY)+File.separator+KEY_FILE_NAME);
		FileOutputStream fos= new FileOutputStream(fout);
		fos.write((shutDownKey+"").getBytes());
		fos.close();
		logger.info("Shutdown key stored to: "+System.getProperty(JAVA_IO_TMPDIR_PROPERTY)+File.separator+KEY_FILE_NAME);
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
		stop();

	}


	public boolean isLocal() {
		return Boolean.valueOf(System.getProperty(LOCAL_MODE_PROPERTY));
	}


	public String getTmpDir() {
		return tmpDir;
	}

	
	public boolean isAuthenticationEnabled() {
		return usesAuthentication;
	}

	// the length of time in ms that a request is valid for, counting from its timestamp value
	public long getMaxRequestTime() {
		return maxRequestTime;
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

	public void setCallbackRegistry(CallbackRegistry callbackRegistry) {
		this.callbackRegistry = callbackRegistry;
	}

	public CallbackRegistry getCallbackRegistry() {
		return callbackRegistry;
	}

	public void setRequestLog(RequestLog requestLog) {
		this.requestLog = requestLog;
	}

	public RequestLog getRequestLog() {
		return requestLog;
	}

	/**
	 * Gets the client store
	 *
	 * @return the client store
	 */
	public  ClientStore<?> getClientStore() {
//		public  <T extends Client>  ClientStore<T> getClientStore() {
		return clientStore;
	}

	/**
	 * Sets the client store
	 *
	 * @param clientStore the new client store
	 */
	public void setClientStore(ClientStore<?> clientStore) {
		this.clientStore = clientStore;

		// TODO for testing only
		if (isAuthenticationEnabled()) {
			Client client = new SimpleClient("clientid", "supersecret", Client.Role.ADMIN, "me@example.org");
			clientStore.add(client);
		}

	}

	private void readOptions() {
		
		String authentication = System.getProperty(AUTHENTICATION_PROPERTY);

		if (authentication != null) {
			if (authentication.equalsIgnoreCase("true")) {
				usesAuthentication = true;
			}
			else if (authentication.equalsIgnoreCase("false")) {
				usesAuthentication = false;
				logger.info("Web service authentication is OFF");
			}
			else {
				logger.error(String.format(
						"Value specified in option %s (%s) is not valid. Using default value of %s.",
						AUTHENTICATION_PROPERTY, authentication, usesAuthentication));
			}
		}

		String tmp = System.getProperty(TMPDIR_PROPERTY);
		if (tmp != null) {
			File f = new File(tmp);
			if (f.exists()) {
				tmpDir = tmp;
			}
			else {
				logger.error(String.format(
						"Value specified in option %s (%s) is not valid. Using default value of %s.",
						TMPDIR_PROPERTY, tmp, tmpDir));
			}
		}

		String maxrequesttime = System.getProperty(MAX_REQUEST_TIME_PROPERTY);
		if (maxrequesttime != null) {
			try {
				long ms = Long.parseLong(maxrequesttime);
				maxRequestTime = ms;
			} catch(NumberFormatException e) {
				logger.error(String.format(
						"Value specified in option %s (%s) is not a valid numeric value. Using default value of %d.",
						MAX_REQUEST_TIME_PROPERTY, maxrequesttime, maxRequestTime));
			}
		}
	}
}
