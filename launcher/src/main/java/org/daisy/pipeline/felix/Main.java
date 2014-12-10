package org.daisy.pipeline.felix;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.Future;

import org.apache.felix.framework.FrameworkFactory;
import org.daisy.pipeline.job.JobManagerFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.launch.Framework;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Main that waits for a Runnable service to arrive it can be run in the main thread.
 * The reason for having this launcher is the contraints imposed by MacOS for launching swt application.
 * The main method was just copy-pasted from org.apache.feilx.main 4.4.0
 *
 * The methods waitForSWT just waits for a runnable service and executes it
 */
public class Main extends org.apache.felix.main.Main{
        /**
         * Switch for specifying bundle directory.
         **/
        public static final String BUNDLE_DIR_SWITCH = "-b";
        /**
         * The property name used to specify whether the launcher should
         * install a shutdown hook.
         **/
        public static final String SHUTDOWN_HOOK_PROP = "felix.shutdown.hook";
        /**
         * The property name used to specify an URL to the system
         * property file.
         **/
        public static final String SYSTEM_PROPERTIES_PROP = "felix.system.properties";
        /**
         * The default name used for the system properties file.
         **/
        public static final String SYSTEM_PROPERTIES_FILE_VALUE = "system.properties";
        /**
         * The property name used to specify an URL to the configuration
         * property file to be used for the created the framework instance.
         **/
        public static final String CONFIG_PROPERTIES_PROP = "felix.config.properties";
        /**
         * The default name used for the configuration properties file.
         **/
        public static final String CONFIG_PROPERTIES_FILE_VALUE = "config.properties";
        /**
         * Name of the configuration directory.
         */
        public static final String CONFIG_DIRECTORY = "conf";

        public static void main(String[] args) throws Exception {
                System.out.println("In main");
                PipelineFramework pf=new PipelineFramework();
                pf.init();
                System.out.println("Getting the futurible");
                Future<JobManagerFactory> future= pf.getService(JobManagerFactory.class);
                System.out.println("Calling get...");
                future.get();
                System.out.println("I got the service!");
        }

        /**
         * Wait for a Runnable service to arrive and execute it
         */
        private static void waitForSWT(Framework fwk) {
                BundleContext bCtxt = fwk.getBundleContext();
                ServiceTracker<Runnable,Runnable> tracker = new ServiceTracker<Runnable,Runnable>(
                                bCtxt, Runnable.class, null);
                Runnable runnable;
                try {
                        tracker.open();
                        runnable = (Runnable)tracker.waitForService(0L);
                        if(runnable!=null){
                                runnable.run();
                        }else{
                                System.err.println("Runnable was null: doing nothing");
                        }
                        tracker.close();
                } catch (Exception e) {
                        System.err.println("Exection in waitForSWT"+e.getMessage());
                        e.printStackTrace();
                }

        }

        /**
         * Simple method to parse META-INF/services file for framework factory.
         * Currently, it assumes the first non-commented line is the class name
         * of the framework factory implementation.
         * @return The created <tt>FrameworkFactory</tt> instance.
         * @throws Exception if any errors occur.
         **/
        static FrameworkFactory getFrameworkFactory() throws Exception
        {
                URL url = Main.class.getClassLoader().getResource(
                                "META-INF/services/org.osgi.framework.launch.FrameworkFactory");
                if (url != null)
                {
                        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                        try
                        {
                                for (String s = br.readLine(); s != null; s = br.readLine())
                                {
                                        s = s.trim();
                                        // Try to load first non-empty, non-commented line.
                                        if ((s.length() > 0) && (s.charAt(0) != '#'))
                                        {
                                                return (FrameworkFactory) Class.forName(s).newInstance();
                                        }
                                }
                        }
                        finally
                        {
                                if (br != null) br.close();
                        }
                }
                throw new Exception("Could not find framework factory.");
        }
} 

