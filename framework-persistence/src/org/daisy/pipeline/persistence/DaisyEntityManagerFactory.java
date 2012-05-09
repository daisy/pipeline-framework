package org.daisy.pipeline.persistence;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DaisyEntityManagerFactory {

	static EntityManagerFactorySupplier supplier;
	protected static Logger logger = LoggerFactory
			.getLogger(EntityManagerFactorySupplier.class.getName());
	public static EntityManager createEntityManager(){
		return  supplier.get().createEntityManager();
	}
	
	public void setEntityManagerFactorySupplier(EntityManagerFactorySupplier supplier){
		logger.debug("EntityManagerFactorySupplier "+supplier);
		DaisyEntityManagerFactory.supplier=supplier;
	}
}
