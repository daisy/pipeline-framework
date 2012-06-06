package org.daisy.pipeline.webservice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import org.daisy.pipeline.job.JobManager;
import org.daisy.pipeline.script.ScriptRegistry;
import org.daisy.pipeline.webservice.clients.ClientStore;
import org.daisy.pipeline.webservice.requestlog.RequestLog;
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
	public static final String LOCAL_MODE_PROPERTY = "org.daisy.pipeline.ws.local";
	public static final String CLIENT_STORE_PROPERTY = "org.daisy.pipeline.ws.clientstore";
	public static final String JAVA_IO_TMPDIR_PROPERTY = "java.io.tmpdir";

	public static final String KEY_FILE_NAME="dp2key.txt";


	public static final String SCRIPTS_ROUTE = "/scripts";
	public static final String SCRIPT_ROUTE = "/scripts/{id}";
	public static final String JOBS_ROUTE = "/jobs";
	public static final String JOB_ROUTE = "/jobs/{id}";
	public static final String LOG_ROUTE = "/jobs/{id}/log";
	public static final String RESULT_ROUTE = "/jobs/{id}/result";
	public static final String HALT_ROUTE = "/admin/halt";
	public static final String CLIENTS_ROUTE = "/admin/clients";
	public static final String CLIENT_ROUTE = "/admin/clients/{id}";

	private static final int LOCAL_PORT_DEF=8181;
	private static final int REMOTE_PORT_DEF=8182;
	/* options and their default values */
	private String path = "/ws";
	private  int portNumber = 0;
	private boolean usesAuthentication = true;
	private long maxRequestTime = 600000; // 10 minutes in ms
	private String tmpDir = "/tmp";

	/** The Constant WS. */
	//private static final String WS = "ws";

	/** The job manager. */
	private JobManager jobManager;

	/** The script registry. */
	private ScriptRegistry scriptRegistry;

    /** The Client Store **/
	private ClientStore clientStore;

	/** The Request Log **/
	private RequestLog requestLog;

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
		if (isAuthenticationEnabled()) {
			// TODO populate some test client
			// client.setId("clientid");
			// client.setSecret("supersecret");
			// client.setContactInfo("me@example.org");
			// client.setRole(PersistentClient.Role.ADMIN);
		}
		logger.info(String.format("Starting webservice on port %d",
				portNumber));
		Component component = new Component();
		component.getServers().add(Protocol.HTTP, portNumber);
		component.getDefaultHost().attach(path, this);
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
	/*
	private void initClientStore() {
		String clientstorefile = System.getProperty(CLIENT_STORE_PROPERTY);
		File file = new File(clientstorefile);
		if (file.exists()) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder documentBuilder;
			try {
				documentBuilder = factory.newDocumentBuilder();
				Document doc = documentBuilder.parse(file);
				if (Validator.validateXml(doc, Validator.clientsSchema)) {
					//DatabaseManager.getInstance().loadData(doc);
				}
				else {
					logger.error(String.format("Could not validate client store file %s", clientstorefile));
				}
			} catch (ParserConfigurationException e) {
				logger.error(e.getMessage());
			} catch (SAXException e) {
				logger.error(e.getMessage());
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
		else {
			logger.error(String.format("Client store file %s not found.", clientstorefile));
		}
	}
	*/
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

	/**
	 * Gets the client store
	 *
	 * @return the client store
	 */
	public ClientStore getClientStore() {
		return clientStore;
	}

	/**
	 * Sets the client store
	 *
	 * @param clientStore the new client store
	 */
	public void setClientStore(ClientStore clientStore) {
		this.clientStore = clientStore;
	}

	/**
	 * @return the requests log
	 */
	public RequestLog getRequestLog() {
		return requestLog;
	}

	/**
	 * @param requestLog the requests log to set
	 */
	public void setRequestLog(RequestLog requestLog) {
		this.requestLog = requestLog;
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

		String port = System.getProperty(PORT_PROPERTY);
		if (port != null) {
			try {
				int portnum = Integer.parseInt(port);
				if (portnum >= 0 && portnum <= 65535) {
					portNumber = portnum;
				}
				else {
					logger.error(String.format(
							"Value specified in option %s (%d) is not valid. Using default value of %d.",
							PORT_PROPERTY, portnum, portNumber));
				}
			} catch (NumberFormatException e) {
				logger.error(String.format(
						"Value specified in option %s (%s) is not a valid numeric value. Using default value of %d.",
						PORT_PROPERTY, port, portNumber));
			}
		}else{
			if (isLocal()){
				portNumber=LOCAL_PORT_DEF;
			}else{
				portNumber=REMOTE_PORT_DEF;
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
