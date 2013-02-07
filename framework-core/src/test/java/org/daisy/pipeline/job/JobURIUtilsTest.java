package org.daisy.pipeline.job;

import java.io.File;

import java.net.URI;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JobURIUtilsTest   {
	JobId id;
	String oldIoBase="";
	File tmpdir;

	@Before
	public void setUp() {
		oldIoBase=System.getProperty(JobURIUtils.ORG_DAISY_PIPELINE_IOBASE);	
		tmpdir= new File(System.getProperty("java.io.tmpdir"));
		System.setProperty(JobURIUtils.ORG_DAISY_PIPELINE_IOBASE,tmpdir.toString());	
		id= JobIdFactory.newId();
		
	}

	@After
	public void tearDown(){
		if(oldIoBase!=null)
			System.setProperty(JobURIUtils.ORG_DAISY_PIPELINE_IOBASE,oldIoBase);	
	}

	@Test
	public void emtpyUriMapper() throws Exception{
		URIMapper mapper = JobURIUtils.newURIMapper();
		Assert.assertEquals(URI.create(""),mapper.getInputBase());
		Assert.assertEquals(URI.create(""),mapper.getOutputBase());

	}
	
	@Test
	public void idBasedUriMapper() throws Exception{
		URIMapper mapper = JobURIUtils.newURIMapper(id);
		String commonBase=tmpdir.toURI().toString()+id.toString()+File.separator;
		Assert.assertEquals(URI.create(commonBase+JobURIUtils.IO_DATA_SUBDIR+File.separator),mapper.getInputBase());
		Assert.assertEquals(URI.create(commonBase+JobURIUtils.IO_OUTPUT_SUBDIR+File.separator),mapper.getOutputBase());

	}

	@Test
	public void getLogFile() throws Exception{
		URI expected= tmpdir.toURI().resolve(URI.create(String.format("%s/%s.log",id.toString(),id.toString())));
		Assert.assertEquals(JobURIUtils.getLogFile(id),expected);

	}
	@Test
	public void getLogFileExsists() throws Exception{
		Assert.assertTrue(new File(JobURIUtils.getLogFile(id)).exists());

	}

	@Test
	public void getJobBase() throws Exception{
		URI expected= tmpdir.toURI().resolve(URI.create(String.format("%s/",id.toString())));
		Assert.assertEquals(JobURIUtils.getJobBase(id),expected);
	}
}
