package org.daisy.pipeline.persistence.mysql;





import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	protected static Logger logger = LoggerFactory
			.getLogger(Activator.class.getName());
	
	public void start(BundleContext bundleContext) throws Exception {
		
		new MysqlEntityManagerFactorySupplier().get().createEntityManager();
		logger.debug("Mysql persistance adapter ready");
	}

	@Override
	public void stop(BundleContext arg0) throws Exception {
		
		
	}
}
