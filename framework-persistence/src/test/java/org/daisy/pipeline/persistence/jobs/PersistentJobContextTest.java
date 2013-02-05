package org.daisy.pipeline.persistence.jobs;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import javax.xml.transform.Source;

import org.daisy.common.base.Provider;

import org.daisy.common.xproc.XProcInput;

import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.job.JobResult;
import org.daisy.pipeline.job.URIMapper;

import org.daisy.pipeline.persistence.Database;

import org.daisy.pipeline.persistence.jobs.Mocks;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class PersistentJobContextTest  {

	Database db;
	PersistentJobContext ctxt;
	JobId id;
	@Before	
	public void setUp(){
		//script setup
			
		ctxt=new PersistentJobContext(Mocks.buildContext());
		id=ctxt.getId();
		db=DatabaseProvider.getDatabase();
		db.addObject(ctxt);
	}
	@After
	public void tearDown(){
		db.deleteObject(ctxt);
	}	
	@Test
	public void storeInput(){
		PersistentJobContext jCtxt= db.getEntityManager().find(PersistentJobContext.class,id.toString());
		Assert.assertEquals(jCtxt.getId(),id);
		Assert.assertEquals(jCtxt.getScriptUri().toString(),Mocks.scriptUri);
		Assert.assertEquals(jCtxt.getLogFile().toString(),Mocks.testLogFile);
	}
	@Test
	public void inputPortsTest(){
		PersistentJobContext jCtxt= db.getEntityManager().find(PersistentJobContext.class,id.toString());
		XProcInput inputs=jCtxt.getInput();
		HashSet<String> expectedSrcs=new HashSet<String>();
		for ( Provider<Source> psrc:inputs.getInputs("source")){
			expectedSrcs.add(psrc.provide().getSystemId());	
		}
		Assert.assertTrue(expectedSrcs.contains(Mocks.file1));
		Assert.assertTrue(expectedSrcs.contains(Mocks.file2));
	}


	@Test
	public void optionTest(){
		PersistentJobContext jCtxt= db.getEntityManager().find(PersistentJobContext.class,id.toString());
		XProcInput inputs=jCtxt.getInput();
		Assert.assertTrue(inputs.getOptions().containsKey(Mocks.opt1Qname));
		Assert.assertTrue(inputs.getOptions().containsKey(Mocks.opt2Qname));
		Assert.assertEquals(inputs.getOptions().get(Mocks.opt1Qname),Mocks.value1);
		Assert.assertEquals(inputs.getOptions().get(Mocks.opt2Qname),Mocks.value2);
	}

	@Test
	public void paramTest(){
		PersistentJobContext jCtxt= db.getEntityManager().find(PersistentJobContext.class,id.toString());
		XProcInput inputs=jCtxt.getInput();
		Assert.assertEquals(inputs.getParameters(Mocks.paramPort).get(new QName(Mocks.qparam)),Mocks.paramVal);
	}

	@Test
	public void mapperTest(){
		PersistentJobContext jCtxt= db.getEntityManager().find(PersistentJobContext.class,id.toString());
		Assert.assertEquals(jCtxt.getMapper(),new URIMapper(Mocks.in,Mocks.out));
	}
	
	@Test
	public void resultPortTest(){
		PersistentJobContext jCtxt= db.getEntityManager().find(PersistentJobContext.class,id.toString());
		List<JobResult> l=new LinkedList<JobResult>(jCtxt.getResults().getResults(Mocks.portResult));
		Assert.assertEquals(l.get(0),Mocks.res1);
	}
	@Test
	public void resultOptionTest(){
		PersistentJobContext jCtxt= db.getEntityManager().find(PersistentJobContext.class,id.toString());
		List<JobResult> l=new LinkedList<JobResult>(jCtxt.getResults().getResults(Mocks.opt1Qname));
		Assert.assertEquals(l.get(0),Mocks.res2);
	}

}
