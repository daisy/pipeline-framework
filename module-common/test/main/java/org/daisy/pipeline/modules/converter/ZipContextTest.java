package org.daisy.pipeline.modules.converter;


import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ZipContextTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test 
	public void testZipContextEntries() throws ZipException, IOException, URISyntaxException{
		HashMap<String,Boolean> res= new HashMap<String, Boolean>();
		res.put("1.txt", false);
		res.put("2.txt", false);
		ZipContext ctxt=new ZipContext(new ZipFile(new File(this.getClass().getClassLoader().getResource("test.zip").toURI())));
		for ( String strRes :ctxt.resources()){
			res.put(strRes, true);
			
		}
		Assert.assertEquals(2, res.values().size());
		for(String strRes:res.keySet()){
			Assert.assertEquals(strRes+" is not in the zip file",true, res.get(strRes));
		}
	}
	

}
