package org.daisy.pipeline.felix;

import java.util.concurrent.Future;

import org.daisy.pipeline.event.EventBusProvider;
import org.daisy.pipeline.job.JobManagerFactory;
import org.daisy.pipeline.script.ScriptRegistry;
import org.daisy.pipeline.webserviceutils.storage.WebserviceStorage;

/**
 * Main that waits for a Runnable service to arrive it can be run in the main thread.
 * The reason for having this launcher is the contraints imposed by MacOS for launching swt application.
 * The main method was just copy-pasted from org.apache.feilx.main 4.4.0
 *
 * The methods waitForSWT just waits for a runnable service and executes it
 */
public class Main {

        public static void main(String[] args) throws Exception {
                System.out.println("In main");
                PipelineFramework pf=new PipelineFramework();
                pf.init();
                System.out.println("Getting the futuribles");
                Future<JobManagerFactory> jManFactorySupplier= pf.getService(JobManagerFactory.class);
                Future<EventBusProvider> eventBusProviderSuppiler=pf.getService(EventBusProvider.class);
                Future<WebserviceStorage> webServiceStorageSuppiler=pf.getService(WebserviceStorage.class);
                Future<ScriptRegistry> scriptRegistrySuppiler=pf.getService(ScriptRegistry.class);
                System.out.println("Doing time comsuming things....");
                System.out.println("Calling gets...");
                jManFactorySupplier.get();
                System.out.println("job manager factory");
                eventBusProviderSuppiler.get();
                System.out.println("event bus supplier");
                webServiceStorageSuppiler.get();
                System.out.println("web service storage");
                scriptRegistrySuppiler.get();
                System.out.println("script registry");
                System.out.println("I got the services!");
        }


} 

