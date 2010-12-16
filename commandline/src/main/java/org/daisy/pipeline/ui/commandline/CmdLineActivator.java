package org.daisy.pipeline.ui.commandline;

import org.daisy.pipeline.ui.commandline.provider.OSGIServiceProvider;
import org.daisy.pipeline.ui.commandline.provider.ServiceProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class CmdLineActivator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		final BundleContext ctxt = context;
		new Thread() {
			public void run() {
				String args = System.getProperty("org.daisy.pipeline.cmdargs");
				ServiceProvider provider = new OSGIServiceProvider(ctxt);
				if (args == null) {
					new CommandLine(provider).getUnrecovreableError(
							"The arguments are null").execute();
					System.exit(1);
				} else {

					try {
						new CommandLine(provider).parse(args.split("\\s"))
								.execute();
					} catch (Exception e) {
						new CommandLine(provider).getUnrecovreableError(e
								.getMessage()).execute();
						System.exit(1);
					}
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
