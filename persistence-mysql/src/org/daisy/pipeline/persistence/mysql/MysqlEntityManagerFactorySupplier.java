package org.daisy.pipeline.persistence.mysql;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.daisy.pipeline.persistence.EntityManagerFactorySupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class MysqlEntityManagerFactorySupplier extends
		EntityManagerFactorySupplier {

	private static final String DAISY_MYSQL = "daisy-mysql";
	private static final String ORG_DAISY_PERSISTENCE_PASSWORD = "org.daisy.pipeline.persistence.password";
	private static final String JAVAX_PERSISTENCE_JDBC_PASSWORD = "javax.persistence.jdbc.password";
	private static final String ORG_DAISY_PERSISTENCE_USER = "org.daisy.pipeline.persistence.user";
	private static final String JAVAX_PERSISTENCE_JDBC_USER = "javax.persistence.jdbc.user";
	private static final String ORG_DAISY_PERSISTENCE_URL = "org.daisy.pipeline.persistence.url";
	private static final String JAVAX_PERSISTENCE_JDBC_URL = "javax.persistence.jdbc.url";
	private static final String JAVAX_PERSISTENCE_JDBC_DRIVER = "javax.persistence.jdbc.driver";
	private static final String COM_MYSQL_JDBC_DRIVER = "com.mysql.jdbc.Driver";
	public static Map<String, String> props = new HashMap<String, String>();
	protected static Logger logger = LoggerFactory
			.getLogger(MysqlEntityManagerFactorySupplier.class.getName());
	static {
		props.put(JAVAX_PERSISTENCE_JDBC_DRIVER,
				COM_MYSQL_JDBC_DRIVER);
		props.put(JAVAX_PERSISTENCE_JDBC_URL,
				System.getProperty(ORG_DAISY_PERSISTENCE_URL));
		props.put(JAVAX_PERSISTENCE_JDBC_USER,
				System.getProperty(ORG_DAISY_PERSISTENCE_USER));
		props.put(JAVAX_PERSISTENCE_JDBC_PASSWORD,
				System.getProperty(ORG_DAISY_PERSISTENCE_PASSWORD));
	}

	@Override
	public EntityManagerFactory get() {
		return Persistence.createEntityManagerFactory(DAISY_MYSQL, props);
	}

}
