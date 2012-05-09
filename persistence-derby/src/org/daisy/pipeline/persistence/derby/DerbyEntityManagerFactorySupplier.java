package org.daisy.pipeline.persistence.derby;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.daisy.pipeline.persistence.EntityManagerFactorySupplier;

public class DerbyEntityManagerFactorySupplier extends EntityManagerFactorySupplier{

	static Map<String,String> props=new HashMap<String, String>();
	static{
		props.put("javax.persistence.jdbc.driver", "org.apache.derby.jdbc.EmbeddedDriver");
		props.put("javax.persistence.jdbc.url", "jdbc:derby:/tmp/db;create=true");
	}
	@Override
	public Map<String, String> getProperties() {
		return props;
	}
	@Override
	public EntityManagerFactory get() {
		return this.getEntityManagerFactoryBuilder().createEntityManagerFactory(this.getProperties());
	}
	
}
