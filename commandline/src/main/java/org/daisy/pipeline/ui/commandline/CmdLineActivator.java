package org.daisy.pipeline.ui.commandline;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.daisy.pipeline.ui.commandline.provider.OSGIServiceProvider;
import org.daisy.pipeline.ui.commandline.provider.ServiceProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class CmdLineActivator implements BundleActivator {
	private static boolean EXIT = false;
	@Override
	public void start(BundleContext context) throws Exception {
		final BundleContext ctxt = context;
		org.slf4j.impl.OSGILogFactory.initOSGI(context);
		
		new Thread() {
			public void run() {
			  //  for (Object key:System.getProperties().keySet()){
			    //	System.out.println("[PROP] "+key+": " + System.getProperties().getProperty(key.toString()));
			    //}
				String args = null;
				args= System.getProperty("org.daisy.pipeline.cmdargs");
				//awful getevn thanks to the disappointing pax runner --vmo space support
				if (args==null)
					args = System.getenv("DAISY_ARGS");
				//System.out.println("[ARGS] "+args);
				ServiceProvider provider = new OSGIServiceProvider(ctxt);
				if (args == null) {
					new CommandLine(provider).getUnrecovreableError(
							"The arguments are null").execute();
					if(EXIT)
						System.exit(1);
				} else {

					try {
						new CommandLine(provider).parse(args.split("\\s"))
								.execute();
					} catch (Exception e) {
						StringWriter sw = new StringWriter();
						e.printStackTrace(new PrintWriter(sw));
						new CommandLine(provider).getUnrecovreableError(e
								.getMessage()+"\n"+sw.toString()).execute();
						if(EXIT)
							System.exit(1);
					}
					if(EXIT)
						System.exit(0);
				}

			}
		}.start();
		
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub

	}

}
