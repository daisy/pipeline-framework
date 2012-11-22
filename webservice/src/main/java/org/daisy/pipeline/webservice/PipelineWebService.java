package org.daisy.pipeline.webservice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import org.daisy.common.properties.PropertyPublisher;
import org.daisy.common.properties.PropertyPublisherFactory;
import org.daisy.common.properties.PropertyTracker;

import org.daisy.pipeline.job.JobManager;
import org.daisy.pipeline.script.ScriptRegistry;
import org.daisy.pipeline.webserviceutils.Properties;
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
import org.restlet.Server;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class PipelineWebService.
 */
public class PipelineWebService extends Application {

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(PipelineWebService.class.getName());
	
	public static final String KEY_FILE_NAME="dp2key.txt";
	PipelineWebServiceConfiguration conf= new PipelineWebServiceConfiguration();
	
	
	/** The job manager. */
	private JobManager jobManager;

	/** The script registry. */
	private ScriptRegistry scriptRegistry;

    /** The Client Store **/
	private ClientStore<?> clientStore;

	private CallbackRegistry callbackRegistry;

	private PropertyPublisher propertyPublisher;
	private long shutDownKey=0L;

	private BundleContext bundleCtxt;

	private RequestLog requestLog;
	private Component component;
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
		router.attach(Routes.ALIVE_ROUTE,AliveResource.class);

		// init the administrative paths
		router.attach(Routes.CLIENTS_ROUTE, ClientsResource.class);
		router.attach(Routes.CLIENT_ROUTE, ClientResource.class);
		router.attach(Routes.HALT_ROUTE, HaltResource.class);
		router.attach(Routes.PROPERTIES_ROUTE, PropertiesResource.class  );

		return router;
	}

	/**
	 * Inits the WS.
	 */
	public void init(BundleContext ctxt) {
		bundleCtxt=ctxt;
		this.conf.publishConfiguration(this.propertyPublisher);
		if (!checkAuthenticationSanity()){

			try {
				this.halt();
			} catch (BundleException e) {
				logger.error("Error shutting down:"+e.getMessage());
			}
			return;
		}
		Routes routes = new Routes();
		
		logger.info(String.format("Starting webservice on port %d",
				routes.getPort()));
		component = new Component();
		
		if (!conf.isSsl()){
			component.getServers().add(Protocol.HTTP, routes.getPort());
			logger.debug("Using HTTP");
		}else{
			Server server = component.getServers().add(Protocol.HTTPS, routes.getPort());
			server.getContext().getParameters().add("keystorePath",conf.getSslKeystore()); 
			server.getContext().getParameters().add("keystorePassword",conf.getSslKeystorePassword());
			server.getContext().getParameters().add("keyPassword",conf.getSslKeyPassword());
			logger.debug("Using HTTPS");
		}
		
		
		component.getDefaultHost().attach(routes.getPath(), this);
		this.setStatusService(new PipelineStatusService());
		try {

			component.start();
			logger.debug("component started");
			generateStopKey();
		} catch (Exception e) {
			logger.error("Shutting down the framework because of:"+e.getMessage());
			try{
				this.halt();
			}catch (Exception innerException) {
				logger.error("Error shutting down:"+e.getMessage());
			}

		}
	}

	private boolean checkAuthenticationSanity() {
		if (this.conf.isAuthenticationEnabled()) {
			//if the clientStore is empty close the 
			//WS
			if (clientStore.getAll().size()==0){
				//no properties supplied
				if (conf.getClientKey()==null || conf.getClientSecret()==null || conf.getClientKey().isEmpty()|| conf.getClientSecret().isEmpty()){
					logger.error("WS mode authenticated but the client store is empty, exiting");
						return false;
				}else{
					//new admin client via configuration properties
					logger.debug("Inserting new client: "+conf.getClientKey());
					clientStore.add(new SimpleClient(conf.getClientKey(),conf.getClientSecret(),Client.Role.ADMIN,"from configuration"));

				}

			}
		}
		return true;

	}
		

	private void generateStopKey() throws IOException {
		shutDownKey = new Random().nextLong();
		File fout = new File(System.getProperty(Properties.JAVA_IO_TMPDIR)+File.separator+KEY_FILE_NAME);
		FileOutputStream fos= new FileOutputStream(fout);
		fos.write((shutDownKey+"").getBytes());
		fos.close();
		logger.info("Shutdown key stored to: "+System.getProperty(Properties.JAVA_IO_TMPDIR)+File.separator+KEY_FILE_NAME);
	}

	public boolean shutDown(long key) throws BundleException{
		if(key==shutDownKey){
			halt();
			return true;
		}
		return false;

	}
	private void halt() throws BundleException{
			((Framework)bundleCtxt.getBundle(0)).stop();
	}
	/**
	 * Close.
	 * @throws Exception
	 * @throws Throwable
	 */
	public void close() throws Exception {
		if (this.component!=null)
			this.component.stop();
		this.stop();
		logger.info("Webservice stopped.");

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

	public PipelineWebServiceConfiguration getConfiguration(){
		return this.conf;	
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

	public void setPropertyPublisherFactory(PropertyPublisherFactory propertyPublisherFactory){
		this.propertyPublisher=propertyPublisherFactory.newPropertyPublisher();	
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

	}

	public PropertyTracker getPropertyTracker(){
		return this.propertyPublisher.getTracker();
	}

}
