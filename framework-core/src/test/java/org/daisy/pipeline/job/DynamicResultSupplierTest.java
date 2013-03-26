package org.daisy.pipeline.job;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import javax.xml.transform.Result;

public class DynamicResultSupplierTest {
	DynamicResultSupplier supplier;
	String pref1="/tmp/file";
	String suf1=".xml";
	@Before
	public void setUp(){
		supplier = new DynamicResultSupplier(pref1,suf1);
	}

	@Test
	public void testGenerateFirst(){
		Result result=supplier.get();
		Assert.assertEquals("/tmp/file.xml",result.getSystemId());
	}

	@Test
	public void testGenerateSecond(){
		Result result=supplier.get();
		result=supplier.get();
		Assert.assertEquals("/tmp/file-1.xml",result.getSystemId());
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testModifyResultObject(){
		supplier.get().setSystemId("sth");
	}
}
