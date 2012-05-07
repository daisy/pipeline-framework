package org.daisy.pipeline.persistence;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.daisy.common.messaging.Message.Level;
import org.daisy.pipeline.job.JobIdFactory;
import org.daisy.pipeline.persistence.messaging.PersistentMessage;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.apache.derby.jdbc.EmbeddedDriver;


public class Activator implements BundleActivator {

	private static BundleContext context;
	private static EntityManager entityManager;
	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		EntityManagerFactory factory =  Persistence.createEntityManagerFactory("daisy");
		entityManager = factory.createEntityManager();
		entityManager.getTransaction().begin();
		PersistentMessage m = new PersistentMessage(null, "hola", Level.INFO, new Date(), 1, "fake-1", 0, 0, "path");
		Client c = new Client();
		c.setContactInfo("my place");
		c.setId("client-1");
		
		entityManager.persist(m);
		entityManager.persist(c);
		entityManager.getTransaction().commit();
		entityManager.close();

		
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

	
	public static EntityManager getEntityManager() {
		
		return entityManager;
	}

}
