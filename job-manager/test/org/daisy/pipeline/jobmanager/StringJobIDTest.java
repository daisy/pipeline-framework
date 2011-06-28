package org.daisy.pipeline.jobmanager;

import junit.framework.Assert;

import org.junit.Test;


public class StringJobIDTest {
	@Test
	public void simpleIdTest(){
		JobID id =new StringJobID.StringIDFactory().getNewID("test");
		Assert.assertEquals(id.getID(), "test-0");
	}
}
