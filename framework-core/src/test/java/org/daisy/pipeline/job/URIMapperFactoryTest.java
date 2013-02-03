package org.daisy.pipeline.job;

import java.io.File;

import java.net.URI;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class URIMapperFactoryTest   {
	JobId id;
	String oldIoBase="";
	File tmpdir;

	@Before
	public void setUp() {
		oldIoBase=System.getProperty(URIMapperFactory.ORG_DAISY_PIPELINE_IOBASE);	
		tmpdir= new File(System.getProperty("java.io.tmpdir"));
		System.setProperty(URIMapperFactory.ORG_DAISY_PIPELINE_IOBASE,tmpdir.toString());	
		id= JobIdFactory.newId();
		
	}

	@After
	public void tearDown(){
		if(oldIoBase!=null)
			System.setProperty(URIMapperFactory.ORG_DAISY_PIPELINE_IOBASE,oldIoBase);	
	}

	@Test
	public void emtpyUriMapper() throws Exception{
		URIMapper mapper = URIMapperFactory.newURIMapper();
		Assert.assertEquals(URI.create(""),mapper.getInputBase());
		Assert.assertEquals(URI.create(""),mapper.getOutputBase());

	}
	
	@Test
	public void idBasedUriMapper() throws Exception{
		URIMapper mapper = URIMapperFactory.newURIMapper(id);
		String commonBase=tmpdir.toURI().toString()+id.toString()+File.separator;
		Assert.assertEquals(URI.create(commonBase+URIMapperFactory.IO_DATA_SUBDIR+File.separator),mapper.getInputBase());
		Assert.assertEquals(URI.create(commonBase+URIMapperFactory.IO_OUTPUT_SUBDIR+File.separator),mapper.getOutputBase());

	}
}
