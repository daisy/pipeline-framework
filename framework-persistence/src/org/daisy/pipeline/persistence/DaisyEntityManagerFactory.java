package org.daisy.pipeline.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DaisyEntityManagerFactory {

	
	public static EntityManager createEntityManager(){
		EntityManagerFactory factory =  Persistence.createEntityManagerFactory("daisy");
		return  factory.createEntityManager();
	}
}
