package org.daisy.pipeline.webservice.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Random;

import org.daisy.common.priority.Priority;
import org.daisy.common.properties.PropertyPublisher;
import org.daisy.common.properties.PropertyPublisherFactory;
import org.daisy.common.properties.PropertyTracker;
import org.daisy.pipeline.clients.Client;
import org.daisy.pipeline.datatypes.DatatypeRegistry;
import org.daisy.pipeline.job.JobBatchId;
import org.daisy.pipeline.job.JobExecutionService;
import org.daisy.pipeline.job.JobManager;
import org.daisy.pipeline.job.JobManagerFactory;
import org.daisy.pipeline.script.ScriptRegistry;
import org.daisy.pipeline.webserviceutils.Properties;
import org.daisy.pipeline.webserviceutils.Routes;
import org.daisy.pipeline.webserviceutils.callback.CallbackRegistry;
import org.daisy.pipeline.webserviceutils.storage.WebserviceStorage;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;
import org.restlet.routing.TemplateRoute;
import org.restlet.routing.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * The Class PipelineWebService.
 */
@org.osgi.service.component.annotations.Component(
    name = "org.daisy.pipeline.webservice",
    immediate = true
)
public class PipelineWebService extends Application {

        /** The logger. */
        private static Logger logger = LoggerFactory.getLogger(PipelineWebService.class.getName());
        
        public static final String KEY_FILE_NAME="dp2key.txt";
        PipelineWebServiceConfiguration conf = new PipelineWebServiceConfiguration();
        
        /** The job manager. */
        private JobManagerFactory jobManagerFactory;

        /** The script registry. */
        private ScriptRegistry scriptRegistry;

        private WebserviceStorage webserviceStorage;
        private CallbackRegistry callbackRegistry;

        private PropertyPublisher propertyPublisher;
        private long shutDownKey=0L;

        private BundleContext bundleCtxt;


        private Component component;

        private JobExecutionService executionQueue;

        private DatatypeRegistry datatypeRegistry;

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
                router.attach(Routes.BATCH_ROUTE, JobBatchResource.class);
                router.attach(Routes.JOB_CONF_ROUTE, JobConfigurationResource.class);
                router.attach(Routes.LOG_ROUTE, LogResource.class);
                router.attach(Routes.RESULT_ROUTE, ResultResource.class);
                router.attach(Routes.RESULT_OPTION_ROUTE     , OptionResultResource.class);
                //:comment This allows to have url-like elements in the idx part of the query   
                TemplateRoute route= router.attach(Routes.RESULT_OPTION_ROUTE_IDX , OptionResultResource.class);
                Map<String, Variable> routeVariables = route.getTemplate().getVariables();
                routeVariables.put("idx", new Variable(Variable.TYPE_URI_ALL));

                
                router.attach(Routes.RESULT_PORT_ROUTE       , PortResultResource.class);
                //goto :comment
                route= router.attach(Routes.RESULT_PORT_ROUTE_IDX , PortResultResource.class);
                routeVariables = route.getTemplate().getVariables();
                routeVariables.put("idx", new Variable(Variable.TYPE_URI_ALL));
                router.attach(Routes.ALIVE_ROUTE,AliveResource.class);

                // init the administrative paths
                router.attach(Routes.CLIENTS_ROUTE, ClientsResource.class);
                router.attach(Routes.CLIENT_ROUTE, ClientResource.class);
                router.attach(Routes.HALT_ROUTE, HaltResource.class);
                router.attach(Routes.PROPERTIES_ROUTE, PropertiesResource.class  );
                router.attach(Routes.SIZES_ROUTE, SizesResource.class  );
                router.attach(Routes.QUEUE_ROUTE, QueueResource.class  );
                router.attach(Routes.QUEUE_UP_ROUTE, QueueUpResource.class  );
                router.attach(Routes.QUEUE_DOWN_ROUTE, QueueDownResource.class  );
                router.attach(Routes.DATATYPE_ROUTE, DatatypeResource.class);
                router.attach(Routes.DATATYPES_ROUTE, DatatypesResource.class);
                return router;
        }

        /**
         * Inits the WS.
         */
        @Activate
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
                        component.getServers().add(Protocol.HTTP, routes.getHost(),routes.getPort());
                        logger.debug("Using HTTP");
                }else{
                        Server server = component.getServers().add(Protocol.HTTPS, routes.getHost(),routes.getPort());
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
                        if (webserviceStorage.getClientStorage().getAll().size()==0){
                                //no properties supplied
                                if (conf.getClientKey()==null || conf.getClientSecret()==null || conf.getClientKey().isEmpty()|| conf.getClientSecret().isEmpty()){
                                        //Making the error more eye catchy      
                                        logger.error("\n"
                                        +"\n"
                                        +"************************************************************\n"
                                        +"WS mode authenticated but the client store is empty, exiting\n"
                                        +"please provide values for the following properties in etc/system.properties: \n"
                                        +"-org.daisy.pipeline.ws.authentication.key    \n"
                                        +"-org.daisy.pipeline.ws.authentication.secret \n"
                                        +"************************************************************\n"
                                        +"\n"
                                        +"\n");
                                                return false;
                                }else{
                                        //new admin client via configuration properties
                                        logger.debug("Inserting new client: "+conf.getClientKey());
                                        this.webserviceStorage.getClientStorage().addClient(conf.getClientKey(),conf.getClientSecret(),Client.Role.ADMIN,"from configuration",Priority.HIGH);

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
        @Deactivate
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
        public JobManager getJobManager(Client client) {
                return jobManagerFactory.createFor(client);
        }
        public JobManager getJobManager(Client client,JobBatchId batchId) {
                return jobManagerFactory.createFor(client,batchId);
        }

        /**
         * Sets the job manager.
         *
         * @param jobManager the new job manager
         */
        @Reference(
           name = "job-manager-factory",
           unbind = "-",
           service = JobManagerFactory.class,
           cardinality = ReferenceCardinality.MANDATORY,
           policy = ReferencePolicy.STATIC
        )
        public void setJobManagerFactory(JobManagerFactory jobManagerFactory) {
                this.jobManagerFactory = jobManagerFactory;
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
        @Reference(
           name = "script-registry",
           unbind = "-",
           service = ScriptRegistry.class,
           cardinality = ReferenceCardinality.MANDATORY,
           policy = ReferencePolicy.STATIC
        )
        public void setScriptRegistry(ScriptRegistry scriptRegistry) {
                this.scriptRegistry = scriptRegistry;
        }

        @Reference(
           name = "callback-registry",
           unbind = "-",
           service = CallbackRegistry.class,
           cardinality = ReferenceCardinality.MANDATORY,
           policy = ReferencePolicy.STATIC
        )
        public void setCallbackRegistry(CallbackRegistry callbackRegistry) {
                this.callbackRegistry = callbackRegistry;
        }


        /**
         * @return the webserviceStorage
         */
        public WebserviceStorage getStorage() {
                return webserviceStorage;
        }

        /**
         * @param webserviceStorage the webserviceStorage to set
         */
        @Reference(
           name = "webservice-storage",
           unbind = "-",
           service = WebserviceStorage.class,
           cardinality = ReferenceCardinality.MANDATORY,
           policy = ReferencePolicy.STATIC
        )
        public void setWebserviceStorage(WebserviceStorage webserviceStorage) {
                this.webserviceStorage = webserviceStorage;
        }

        /**
         * @param ExecutionQueue 
         */
        public void setExecutionQueue(JobExecutionService executionQueue) {
                this.executionQueue= executionQueue;
        }

        public JobExecutionService getExecutionQueue() {
                return this.executionQueue;
        }

        public CallbackRegistry getCallbackRegistry() {
                return callbackRegistry;
        }

        @Reference(
           name = "",
           unbind = "unsetPropertyPublisherFactory",
           service = PropertyPublisherFactory.class,
           cardinality = ReferenceCardinality.MANDATORY,
           policy = ReferencePolicy.DYNAMIC
        )
        public void setPropertyPublisherFactory(PropertyPublisherFactory propertyPublisherFactory){
                this.propertyPublisher=propertyPublisherFactory.newPropertyPublisher(); 
        }

        public void unsetPropertyPublisherFactory(PropertyPublisherFactory propertyPublisherFactory){
                this.propertyPublisher=propertyPublisherFactory.newPropertyPublisher(); 
                this.conf.unpublishConfiguration(this.propertyPublisher);
                this.propertyPublisher=null;
        }
        /**
         * Gets the client store
         *
         * @return the client store
         */
        public PropertyTracker getPropertyTracker(){
                if(this.propertyPublisher == null)
                        return null;
                return this.propertyPublisher.getTracker();
        }

        @Reference(
           name = "datatype-registry",
           unbind = "-",
           service = DatatypeRegistry.class,
           cardinality = ReferenceCardinality.MANDATORY,
           policy = ReferencePolicy.STATIC
        )
        public void setDatatypeRegistry(DatatypeRegistry datatypeRegistry){
                this.datatypeRegistry=datatypeRegistry;
        }
        public DatatypeRegistry getDatatypeRegistry(){
                return this.datatypeRegistry;
        }

}
