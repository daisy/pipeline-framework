package org.daisy.pipeline.persistence;

import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.osgi.service.jpa.EntityManagerFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Supplier;

/**
 * 
 * the actual get has to be in the configuration bundle in order to invoke the correct driver
 * @author javi
 *
 */
public abstract class EntityManagerFactorySupplier implements Supplier<EntityManagerFactory>{
	protected static Logger logger = LoggerFactory
			.getLogger(EntityManagerFactorySupplier.class.getName());
	private EntityManagerFactoryBuilder builder;

	public abstract Map<String,String> getProperties();
	
	
	
	public EntityManagerFactoryBuilder getEntityManagerFactoryBuilder() {
		return builder;
	}

	public void setEntityManagerFactoryBuilder(EntityManagerFactoryBuilder builder) {
		logger.debug("Setting entity factory builder");
		this.builder = builder;
		//init
		this.get().createEntityManager();
	}
	
	
	
}
