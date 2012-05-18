package org.daisy.pipeline.persistence;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Database {

	private EntityManager entityManager = null;

	/** The logger. */
	private static Logger logger = LoggerFactory
			.getLogger(Database.class.getName());

	private void openDB() {

		try {
			entityManager = DaisyEntityManagerFactory.createEntityManager();
		} catch (Exception e) {
			logger.error("Database exception " + e.getMessage());
		}
	}

	private void closeDB() {
		if (entityManager != null) {
			entityManager.close();
		}
		
	}

	public void addObject(Object obj) {
		try {
			openDB();
			entityManager.getTransaction().begin();
			entityManager.persist(obj);
			entityManager.getTransaction().commit();
		} finally {
			closeDB();
		}
	}

	public boolean deleteObject(Object obj) {
		boolean retval = false;
		try {
			openDB();
			entityManager.getTransaction().begin();
			
			if (obj != null) {
				entityManager.remove(obj);
				retval = true;
			}
			entityManager.getTransaction().commit();
		} finally {
			closeDB();
		}
		return retval;
	}

	public void updateObject(Object obj) {
		openDB();
		entityManager.getTransaction().begin();
		entityManager.merge(obj);
		entityManager.getTransaction().commit();
		closeDB();
	}

	

	@SuppressWarnings("unchecked")//this how JPA works
	public <T> List<T> runQuery(String queryString,Class<T> clazz) {
		List<T> list = null;
		try {
			openDB();
			Query q = entityManager.createQuery(queryString);
			list = (List<T>)q.getResultList();
		} finally {
			closeDB();
		}
		return list;
	}

	@SuppressWarnings("unchecked")//this how JPA works
	public <T> T getFirst(String queryString,Class<T> clazz) {
		T obj = null;
		try {
			openDB();
			Query q = entityManager.createQuery(queryString);
			obj =  (T) q.getSingleResult();
		} finally {
			closeDB();
		}
		return obj;
	}
}
