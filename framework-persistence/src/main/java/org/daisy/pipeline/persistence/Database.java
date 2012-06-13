package org.daisy.pipeline.persistence;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Database {

	private EntityManager entityManager = null;

	private static Logger logger = LoggerFactory.getLogger(Database.class
			.getName());

	public void addObject(Object obj) {
		entityManager.getTransaction().begin();
		entityManager.persist(obj);
		entityManager.getTransaction().commit();
	}

	public boolean deleteObject(Object obj) {
		if (obj != null) {
			entityManager.getTransaction().begin();
			entityManager.remove(obj);
			entityManager.getTransaction().commit();
			return true;
		}
		return false;
	}

	public void updateObject(Object obj) {
		entityManager.getTransaction().begin();
		entityManager.merge(obj);
		entityManager.getTransaction().commit();
	}

	public <T> List<T> runQuery(String queryString, Class<T> clazz) {
		TypedQuery<T> q = entityManager.createQuery(queryString, clazz);
		return q.getResultList();
	}

	public <T> T getFirst(String queryString, Class<T> clazz) {
		TypedQuery<T> q = entityManager.createQuery(queryString, clazz);
		return q.getSingleResult();
	}

	public void setEntityManagerFactory(EntityManagerFactory emf) {
		try {
			entityManager = emf.createEntityManager();
		} catch (Exception e) {
			logger.error("Database exception " + e.getMessage());
		}
	}

	public void deactivate() {
		if (entityManager != null) {
			entityManager.close();
		}
	}
}
