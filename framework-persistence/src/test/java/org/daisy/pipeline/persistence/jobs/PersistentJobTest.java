package org.daisy.pipeline.persistence.jobs;

import java.util.Collection;
import java.util.List;

import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.JobId;

import org.daisy.pipeline.persistence.Database;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

public class PersistentJobTest   {

	Database db;
	JobId id;
	PersistentJob job;
	@Before	
	public void setUp(){
		//script setup
			
		job = new PersistentJob(Job.newJob(Mocks.buildContext()),db);
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
	@Test 
	public void changeStatusTest(){
		PersistentJob pjob= db.getEntityManager().find(PersistentJob.class,id.toString());
		pjob.setDatabase(db);
		pjob.changeStatus(Job.Status.DONE);
		//System.out.println("pre udpate");
		db.updateObject(pjob);
	
		//System.out.println("status post update"+pjob.getStatus());
		pjob= db.getEntityManager().find(PersistentJob.class,id.toString());
		Assert.assertEquals(pjob.getStatus(),Job.Status.DONE);

	}
	@Test 
	public void getJobsTest(){
		List<Job> jobs=PersistentJob.getAllJobs(db);
		Assert.assertEquals(jobs.get(0).getContext().getId(),id);

	}

}
