package org.daisy.pipeline.persistence.derby;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.daisy.pipeline.persistence.EntityManagerFactorySupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class DerbyEntityManagerFactorySupplier extends EntityManagerFactorySupplier{

	public static Map<String,String> props=new HashMap<String, String>();
	protected static Logger logger = LoggerFactory
			.getLogger(DerbyEntityManagerFactorySupplier.class.getName());
	static{
		props.put("javax.persistence.jdbc.driver", "org.apache.derby.jdbc.EmbeddedDriver");
		props.put("javax.persistence.jdbc.url", "jdbc:derby:"+System.getProperty("org.daisy.pipeline.home")+"/data/db;create=true");
		logger.debug("jdbc:derby:"+System.getProperty("org.daisy.pipeline.home")+"/data/db;create=true");
	}
	
	@Override
	public EntityManagerFactory get() {
		return Persistence.createEntityManagerFactory("daisy-derby",props);
	}
	
}
