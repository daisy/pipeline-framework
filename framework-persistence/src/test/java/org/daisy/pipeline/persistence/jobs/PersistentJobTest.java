package org.daisy.pipeline.persistence.jobs;

import org.daisy.pipeline.job.JobId;

import org.daisy.pipeline.persistence.Database;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PersistentJobTest   {

	Database db;
	JobId id;
	PersistentJob job;
	@Before	
	public void setUp(){
		//script setup
			
		job = new PersistentJob(Mocks.buildContext());
		id=job.getContext().getId();
		db=DatabaseProvider.getDatabase();
		db.addObject(job);
	}
	@After
	public void tearDown(){
		db.deleteObject(job);
	}	

	@Test 
	public void storeTest(){
		PersistentJob pjob= db.getEntityManager().find(PersistentJob.class,id.toString());
		Assert.assertEquals(pjob.getContext().getId(),id);

	}

}
