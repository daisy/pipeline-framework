package org.daisy.pipeline.persistence.services;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import com.google.common.base.Supplier;


public class EntityManagerProvider implements Supplier<EntityManager>{

	private static EntityManager mEm;
	static{
		mEm=Persistence.createEntityManagerFactory("daisy").createEntityManager();
	}

	
	
	@Override
	public EntityManager get() {
		return mEm;
	}

}
