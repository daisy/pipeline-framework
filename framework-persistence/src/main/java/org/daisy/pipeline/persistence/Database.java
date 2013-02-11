package org.daisy.pipeline.persistence;

import java.util.List;

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Database {

	//private this.getEntityManager() this.getEntityManager() = null;
	private EntityManagerFactory emf=null;
	private static Logger logger = LoggerFactory.getLogger(Database.class
			.getName());

	public void addObject(Object obj) {
		EntityManager em=this.getEntityManager();
		em.getTransaction().begin();
		em.persist(obj);
		em.getTransaction().commit();
		em.close();
	}

	public boolean deleteObject(Object obj) {
		if (obj != null) {
			EntityManager em=this.getEntityManager();
			em.getTransaction().begin();
			em.remove(em.merge(obj));
			em.getTransaction().commit();
			em.close();
			return true;
		}
		return false;
	}

	public void updateObject(Object obj) {
		EntityManager em=this.getEntityManager();
		em.getTransaction().begin();
		em.merge(obj);
		em.getTransaction().commit();
		em.close();
	}

	public <T> List<T> runQuery(String queryString, Class<T> clazz) {
		EntityManager em= emf.createEntityManager();
		TypedQuery<T> q = em.createQuery(queryString, clazz);
		List<T> res= q.getResultList();
		em.close();
		return res;
	}

	public <T> T getFirst(String queryString, Class<T> clazz) {
		EntityManager em= emf.createEntityManager();
		TypedQuery<T> q = em.createQuery(queryString, clazz);
		T res=q.getSingleResult();
		em.close();
		return res;
	}

	public EntityManager getEntityManager(){
		if (emf!=null)
			return emf.createEntityManager();
		else
			throw new IllegalStateException("entity manager factory was null");
	}

	public Cache getCache(){
		if (emf!=null)
			return emf.getCache();
		else
			throw new IllegalStateException("entity manager factory was null");
	}

	public void setEntityManagerFactory(EntityManagerFactory emf) {
		this.emf=emf;
	}

	
}
