package org.daisy.pipeline.persistence.derby;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.daisy.pipeline.persistence.DaisyEntityManagerFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator {

	
	protected static Logger logger = LoggerFactory
			.getLogger(Activator.class.getName());
	
	public void start(BundleContext bundleContext) throws Exception {
		
		new DerbyEntityManagerFactorySupplier().get().createEntityManager();
		logger.debug("Derby persistance adapter ready");
	}

	@Override
	public void stop(BundleContext arg0) throws Exception {
		
		
	}

	
	

}
