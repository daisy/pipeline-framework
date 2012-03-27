package org.daisy.pipeline.persistence.services;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.daisy.common.base.Provider;


public class EntityManagerProvider implements Provider<EntityManager>{

	private static EntityManager mEm;
	static{
		mEm=Persistence.createEntityManagerFactory("daisy").createEntityManager();
	}

	
	
	@Override
	public EntityManager provide() {
		return mEm;
	}

}
