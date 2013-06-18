package org.daisy.pipeline.nonpersistent.jobs;

import java.util.HashSet;
import java.util.Set;

import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.JobContext;
import org.daisy.pipeline.job.JobId;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class VolatileJobStorageTest   {
	JobContext ctxt1;
	JobContext ctxt2;
	VolatileJobStorage storage;
	String oldBase="";
	@Before
	public void setUp(){
		oldBase=System.getProperty("org.daisy.pipeline.iobase","");
		System.setProperty("org.daisy.pipeline.iobase",System.getProperty("java.io.tmpdir"));
		storage=new VolatileJobStorage();
		ctxt1= Mock.newJobContext();
		ctxt2= Mock.newJobContext();
	}

	@After
	public void tearDown(){
		System.setProperty("org.daisy.pipeline.iobase",oldBase);
	}

	@Test
	public void add() {
		Job job=this.storage.add(ctxt1);
		Assert.assertEquals(ctxt1.getId(),job.getId());

	}


	@Test
	public void get() {
		this.storage.add(ctxt1);
		Job job = this.storage.get(ctxt1.getId());
		Assert.assertEquals(ctxt1.getId(),job.getId());
	}

	@Test
	public void getNotPresent() {
		Job job = this.storage.get(ctxt1.getId());
		Assert.assertNull(job);
	}

	@Test
	public void iterator() {
		this.storage.add(ctxt1);
		this.storage.add(ctxt2);
		Set<JobId> jobs=new HashSet<JobId>();
		for(Job j: this.storage){
			jobs.add(j.getId());
		}
		Assert.assertEquals(2,jobs.size());
		Assert.assertTrue(jobs.contains(ctxt1.getId()));
		Assert.assertTrue(jobs.contains(ctxt2.getId()));
	}

	@Test
	public void remove() {
		this.storage.add(ctxt1);
		this.storage.remove(ctxt1.getId());
		Job job = this.storage.get(ctxt1.getId());
		Assert.assertNull(job);
	}

	@Test
	public void removeNotPresent() {
		Job job=this.storage.remove(ctxt1.getId());
		Assert.assertNull(job);
	}
}
