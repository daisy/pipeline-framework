package org.daisy.pipeline.persistence.derby;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	

	
	public void start(BundleContext bundleContext) throws Exception {
		//init stuff to create the data base
		//EntityManagerFactory emf = new DerbyEntityManagerFactorySupplier().get();
		//EntityManager em = emf.createEntityManager();
		int paco=0;
	}

	@Override
	public void stop(BundleContext arg0) throws Exception {
		
		
	}

	
	

}
