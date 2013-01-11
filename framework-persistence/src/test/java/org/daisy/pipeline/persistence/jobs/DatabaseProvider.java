package org.daisy.pipeline.persistence.jobs;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.daisy.pipeline.persistence.Database;

public class DatabaseProvider {
	private static EntityManagerFactory entityManagerFactory=Persistence.createEntityManagerFactory("pipeline-pu-test");

	public static Database getDatabase(){
		Database db= new Database();
		db.setEntityManagerFactory(entityManagerFactory);
		return db;
	}

}
