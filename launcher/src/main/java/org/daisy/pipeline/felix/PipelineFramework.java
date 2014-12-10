package org.daisy.pipeline.felix;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.felix.framework.FrameworkFactory;
import org.apache.felix.main.AutoProcessor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.launch.Framework;
import org.osgi.util.tracker.ServiceTracker;

public class PipelineFramework {

        private Framework fwk = null;
        private ExecutorService executor= new ThreadPoolExecutor(3, 3, 10, TimeUnit.HOURS,new LinkedBlockingQueue<Runnable>());
        //Modifications on the main method from felix
        public void init(){
                // Look for bundle directory and/or cache directory.
                // We support at most one argument, which is the bundle
                // cache directory.
                // Load system properties.
                Main.loadSystemProperties();
                // Read configuration properties.
                Map<String, String> configProps = Main.loadConfigProperties();
                // If no configuration properties were found, then create
                // an empty properties object.
                if (configProps == null) {
                        System.err
                                        .println("No " + Main.CONFIG_PROPERTIES_FILE_VALUE + " found.");
                        configProps = new HashMap<String, String>();
                }
                // Copy framework properties from the system properties.
                Main.copySystemProperties(configProps);
                try {
                        // Create an instance of the framework.
                        FrameworkFactory factory = Main.getFrameworkFactory();
                        fwk = factory.newFramework(configProps);
                        // Initialize the framework, but don't start it yet.
                        fwk.init();
                        // Use the system bundle context to process the auto-deploy
                        // and auto-install/auto-start properties.
                        AutoProcessor.process(configProps, fwk.getBundleContext());
                                // Start the framework.
                                fwk.start();
                } catch (Exception ex) {
                        System.err.println("Could not create framework: " + ex);
                        throw new RuntimeException(ex);
                }

        }

        public <T> Future<T>  getService(final Class<T> serviceClass){
                Callable<T> callable= new Callable<T>(){
                        public T call(){
                                BundleContext bCtxt = fwk.getBundleContext();
                                ServiceTracker<T,T> tracker = new ServiceTracker<T,T>(
                                                bCtxt, serviceClass, null);
                                T service;
                                try {
                                        System.out.println("Opening tracker!");
                                        tracker.open();
                                        service= (T)tracker.waitForService(0L);
                                        System.out.println(String.format("Got the service! %s",service));
                                        tracker.close();
                                } catch (Exception e){
                                        throw new RuntimeException(e);
                                }
                                System.out.println("returning the service!");
                                return service;
                        }
                };


                Future<T> future=this.executor.submit(callable);
                return future;

        }

}
