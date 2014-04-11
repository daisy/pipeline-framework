package org.daisy.pipeline.swt.launcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.felix.framework.FrameworkFactory;
import org.apache.felix.framework.util.Util;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.launch.Framework;
import org.osgi.util.tracker.ServiceTracker;

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
        private static Framework m_fwk = null;

        /**
         * <p>
         * This method performs the main task of constructing an framework instance
         * and starting its execution. The following functions are performed
         * when invoked:
         * </p>
         * <ol>
         * <li><i><b>Examine and verify command-line arguments.</b></i> The launcher
         * accepts a "<tt>-b</tt>" command line switch to set the bundle auto-deploy
         * directory and a single argument to set the bundle cache directory.
         * </li>
         * <li><i><b>Read the system properties file.</b></i> This is a file
         * containing properties to be pushed into <tt>System.setProperty()</tt>
         * before starting the framework. This mechanism is mainly shorthand
         * for people starting the framework from the command line to avoid having
         * to specify a bunch of <tt>-D</tt> system property definitions.
         * The only properties defined in this file that will impact the framework's
         * behavior are the those concerning setting HTTP proxies, such as
         * <tt>http.proxyHost</tt>, <tt>http.proxyPort</tt>, and
         * <tt>http.proxyAuth</tt>. Generally speaking, the framework does
         * not use system properties at all.
         * </li>
         * <li><i><b>Read the framework's configuration property file.</b></i> This is
         * a file containing properties used to configure the framework
         * instance and to pass configuration information into
         * bundles installed into the framework instance. The configuration
         * property file is called <tt>config.properties</tt> by default
         * and is located in the <tt>conf/</tt> directory of the Felix
         * installation directory, which is the parent directory of the
         * directory containing the <tt>felix.jar</tt> file. It is possible
         * to use a different location for the property file by specifying
         * the desired URL using the <tt>felix.config.properties</tt>
         * system property; this should be set using the <tt>-D</tt> syntax
         * when executing the JVM. If the <tt>config.properties</tt> file
         * cannot be found, then default values are used for all configuration
         * properties. Refer to the
         * <a href="Felix.html#Felix(java.util.Map)"><tt>Felix</tt></a>
         * constructor documentation for more information on framework
         * configuration properties.
         * </li>
         * <li><i><b>Copy configuration properties specified as system properties
         * into the set of configuration properties.</b></i> Even though the
         * Felix framework does not consult system properties for configuration
         * information, sometimes it is convenient to specify them on the command
         * line when launching Felix. To make this possible, the Felix launcher
         * copies any configuration properties specified as system properties
         * into the set of configuration properties passed into Felix.
         * </li>
         * <li><i><b>Add shutdown hook.</b></i> To make sure the framework shutdowns
         * cleanly, the launcher installs a shutdown hook; this can be disabled
         * with the <tt>felix.shutdown.hook</tt> configuration property.
         * </li>
         * <li><i><b>Create and initialize a framework instance.</b></i> The OSGi standard
         * <tt>FrameworkFactory</tt> is retrieved from <tt>META-INF/services</tt>
         * and used to create a framework instance with the configuration properties.
         * </li>
         * <li><i><b>Auto-deploy bundles.</b></i> All bundles in the auto-deploy
         * directory are deployed into the framework instance.
         * </li>
         * <li><i><b>Start the framework.</b></i> The framework is started and
         * the launcher thread waits for the framework to shutdown.
         * </li>
         * </ol>
         * <p>
         * It should be noted that simply starting an instance of the framework is not
         * enough to create an interactive session with it. It is necessary to install
         * and start bundles that provide a some means to interact with the framework;
         * this is generally done by bundles in the auto-deploy directory or specifying
         * an "auto-start" property in the configuration property file. If no bundles
         * providing a means to interact with the framework are installed or if the
         * configuration property file cannot be found, the framework will appear to
         * be hung or deadlocked. This is not the case, it is executing correctly,
         * there is just no way to interact with it.
         * </p>
         * <p>
         * The launcher provides two ways to deploy bundles into a framework at
         * startup, which have associated configuration properties:
         * </p>
         * <ul>
         * <li>Bundle auto-deploy - Automatically deploys all bundles from a
         * specified directory, controlled by the following configuration
         * properties:
         * <ul>
         * <li><tt>felix.auto.deploy.dir</tt> - Specifies the auto-deploy directory
         * from which bundles are automatically deploy at framework startup.
         * The default is the <tt>bundle/</tt> directory of the current directory.
         * </li>
         * <li><tt>felix.auto.deploy.action</tt> - Specifies the auto-deploy actions
         * to be found on bundle JAR files found in the auto-deploy directory.
         * The possible actions are <tt>install</tt>, <tt>update</tt>,
         * <tt>start</tt>, and <tt>uninstall</tt>. If no actions are specified,
         * then the auto-deploy directory is not processed. There is no default
         * value for this property.
         * </li>
         * </ul>
         * </li>
         * <li>Bundle auto-properties - Configuration properties which specify URLs
         * to bundles to install/start:
         * <ul>
         * <li><tt>felix.auto.install.N</tt> - Space-delimited list of bundle
         * URLs to automatically install when the framework is started,
         * where <tt>N</tt> is the start level into which the bundle will be
         * installed (e.g., felix.auto.install.2).
         * </li>
         * <li><tt>felix.auto.start.N</tt> - Space-delimited list of bundle URLs
         * to automatically install and start when the framework is started,
         * where <tt>N</tt> is the start level into which the bundle will be
         * installed (e.g., felix.auto.start.2).
         * </li>
         * </ul>
         * </li>
         * </ul>
         * <p>
         * These properties should be specified in the <tt>config.properties</tt>
         * so that they can be processed by the launcher during the framework
         * startup process.
         * </p>
         * @param args Accepts arguments to set the auto-deploy directory and/or
         * the bundle cache directory.
         * @throws Exception If an error occurs.
         **/
        public static void main(String[] args) throws Exception {
                // Look for bundle directory and/or cache directory.
                // We support at most one argument, which is the bundle
                // cache directory.
                String bundleDir = null;
                String cacheDir = null;
                boolean expectBundleDir = false;
                for (int i = 0; i < args.length; i++) {
                        if (args[i].equals(BUNDLE_DIR_SWITCH)) {
                                expectBundleDir = true;
                        } else if (expectBundleDir) {
                                bundleDir = args[i];
                                expectBundleDir = false;
                        } else {
                                cacheDir = args[i];
                        }
                }
                if ((args.length > 3) || (expectBundleDir && bundleDir == null)) {
                        System.out
                                        .println("Usage: [-b <bundle-deploy-dir>] [<bundle-cache-dir>]");
                        System.exit(0);
                }
                // Load system properties.
                Main.loadSystemProperties();
                // Read configuration properties.
                Map<String, String> configProps = Main.loadConfigProperties();
                // If no configuration properties were found, then create
                // an empty properties object.
                if (configProps == null) {
                        System.err
                                        .println("No " + CONFIG_PROPERTIES_FILE_VALUE + " found.");
                        configProps = new HashMap<String, String>();
                }
                // Copy framework properties from the system properties.
                Main.copySystemProperties(configProps);
                // If there is a passed in bundle auto-deploy directory, then
                // that overwrites anything in the config file.
                if (bundleDir != null) {
                        configProps.put(AutoProcessor.AUTO_DEPLOY_DIR_PROPERY, bundleDir);
                }
                // If there is a passed in bundle cache directory, then
                // that overwrites anything in the config file.
                if (cacheDir != null) {
                        configProps.put(Constants.FRAMEWORK_STORAGE, cacheDir);
                }
                try {
                        // Create an instance of the framework.
                        FrameworkFactory factory = getFrameworkFactory();
                        m_fwk = factory.newFramework(configProps);
                        // Initialize the framework, but don't start it yet.
                        m_fwk.init();
                        // Use the system bundle context to process the auto-deploy
                        // and auto-install/auto-start properties.
                        AutoProcessor.process(configProps, m_fwk.getBundleContext());
                        FrameworkEvent event;
                        do {
                                // Start the framework.
                                m_fwk.start();
                                waitForSWT(m_fwk);
                                // Wait for framework to stop to exit the VM.
                                event = m_fwk.waitForStop(0);
                        }
                        // If the framework was updated, then restart it.
                        while (event.getType() == FrameworkEvent.STOPPED_UPDATE);
                        // Otherwise, exit.
                        System.exit(0);
                } catch (Exception ex) {
                        System.err.println("Could not create framework: " + ex);
                        ex.printStackTrace();
                        System.exit(0);
                }
        }

        private static void waitForSWT(Framework fwk) {
                BundleContext bCtxt = fwk.getBundleContext();
                ServiceTracker tracker = new ServiceTracker(
                                bCtxt, Runnable.class, null);
                Runnable runnable;
                try {
                        tracker.open();
                        System.out.println("Waiting for the SWT service");
                        runnable = (Runnable)tracker.waitForService(-0L);
                        if(runnable!=null){
                                System.out.println("About to run the runnable");
                                runnable.run();
                        }else{
                                System.out.println("Runnable was null ¬¬");
                        }
                        tracker.close();
                } catch (Exception e) {
                        System.out.println("This is bad:");
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
        private static FrameworkFactory getFrameworkFactory() throws Exception
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

