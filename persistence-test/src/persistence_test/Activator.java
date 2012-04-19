package persistence_test;



import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;

				        

		//Activator.context.getClass().getClassLoader().loadClass("org.sqlite.JDBC");
		EntityManagerFactory factory =  Persistence.createEntityManagerFactory("test");
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		Dogs hound = new Dogs();
		hound.setName("Snowball");
		//hound.setId(1L);
		em.persist(hound);
		em.getTransaction().commit();

		em.close();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
