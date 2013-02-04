package org.daisy.pipeline.job;

import java.io.File;
import java.io.IOException;

import java.net.URI;

import java.util.HashSet;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import org.daisy.common.base.Provider;

import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcOutput;

import org.daisy.pipeline.script.XProcScript;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

public class ResultSetFactoryTest {
	URIMapper mapper;	
	XProcScript script;
	ResultSet.Builder builder ;
	XProcOutput output;
	XProcInput input;
	String sysId="dir/file.xml";
	String dir="option/";
	@Before
	public void setUp() throws IOException{
		script= new Mock.ScriptGenerator.Builder().withOutputPorts(2).withOptionOutputsFile(1).withOptionOutputsDir(1).build().generate();
		URI tmp=new File(System.getProperty("java.io.tmpdir")).toURI();
		mapper = new URIMapper(tmp.resolve("inputs/"),tmp.resolve("outputs/"));
		builder = new ResultSet.Builder();

		String outName = Mock.ScriptGenerator.getOutputName(0);
		XProcOutput outs = new XProcOutput.Builder().withOutput(outName,Mock.getResultProvider(sysId)).build();
		XProcDecorator trans=XProcDecorator.from(script,mapper);
		output=trans.decorate(outs);

		QName optDir=Mock.ScriptGenerator.getOptionOutputDirName(0);
		QName optName=Mock.ScriptGenerator.getOptionOutputFileName(0);
		input = new XProcInput.Builder().withOption(optDir,dir).withOption(optName,sysId).build();
		input=trans.decorate(input);
		Mock.populateDir(input.getOptions().get(optDir));
	}


	@After
	public void tearDown() {
		QName optDir=Mock.ScriptGenerator.getOptionOutputDirName(0);
		IOHelper.deleteDir(new File(input.getOptions().get(optDir)));
				
	}

	@Test 
	public void ouputPort() throws Exception{

		String outName = Mock.ScriptGenerator.getOutputName(0);
		Provider<Result> res=output.getResultProvider(outName);
		res.provide();
		
		ResultSetFactory.collectOutputs(script,output,mapper,builder);
		ResultSet rSet=builder.build();
		List<JobResult> jobs=Lists.newLinkedList(rSet.getResults(outName));
		Assert.assertEquals(mapper.mapOutput(URI.create(sysId)),jobs.get(0).getPath());
		Assert.assertEquals(sysId,jobs.get(0).getIdx());

	}

	@Test 
	public void ouputPortNullCheck() throws Exception{

		String outName = Mock.ScriptGenerator.getOutputName(0);
		XProcOutput output = new XProcOutput.Builder().build();
		
		ResultSetFactory.collectOutputs(script,output,mapper,builder);
		ResultSet rSet=builder.build();
		List<JobResult> jobs=Lists.newLinkedList(rSet.getResults(outName));
		Assert.assertEquals(jobs.size(),0);

	}

	@Test 
	public void ouputPortSequence() throws Exception{

		String outName = Mock.ScriptGenerator.getOutputName(1);
		Provider<Result> res=output.getResultProvider(outName);
		res.provide();
		res.provide();
		
		ResultSetFactory.collectOutputs(script,output,mapper,builder);
		ResultSet rSet=builder.build();
		List<JobResult> jobs=Lists.newLinkedList(rSet.getResults(outName));
		Assert.assertEquals(jobs.size(),2);

	}

	@Test
	public void dynamicProviderResults() throws Exception{

		String outName = Mock.ScriptGenerator.getOutputName(0);
		DynamicResultProvider res=(DynamicResultProvider) output.getResultProvider(outName);
		res.provide();
		res.provide();
		List<JobResult> jobs=ResultSetFactory.buildJobResult(res,mapper);
		Assert.assertEquals(jobs.size(),2);
		Assert.assertEquals(mapper.mapOutput(URI.create(sysId)),jobs.get(0).getPath());
		Assert.assertEquals(sysId,jobs.get(0).getIdx());
		
	}

	@Test
	public void nonDynamicProviderResults() throws Exception{
		String outName = Mock.ScriptGenerator.getOutputName(0);
		Provider<Result> res= output.getResultProvider(outName);
		List<JobResult> jobs=ResultSetFactory.buildJobResult(res,mapper);
		Assert.assertEquals(mapper.mapOutput(URI.create(sysId)),jobs.get(0).getPath());
		Assert.assertEquals(sysId,jobs.get(0).getIdx());
		
	}

	@Test
	public void optionsOutputFile() throws Exception{
		QName optName=Mock.ScriptGenerator.getOptionOutputFileName(0);
		ResultSetFactory.collectOptions(script,input,mapper,builder);
		ResultSet rSet=builder.build();
		List<JobResult> jobs=Lists.newLinkedList(rSet.getResults(optName));
		Assert.assertEquals(mapper.mapOutput(URI.create(sysId)),jobs.get(0).getPath());
		Assert.assertEquals(sysId,jobs.get(0).getIdx());
		
	}

	@Test
	public void optionsOutputDirSize() throws Exception{
		QName optName=Mock.ScriptGenerator.getOptionOutputDirName(0);
		ResultSetFactory.collectOptions(script,input,mapper,builder);
		ResultSet rSet=builder.build();
		List<JobResult> jobs=Lists.newLinkedList(rSet.getResults(optName));
		Assert.assertEquals(3,jobs.size());

		
	}

	@Test
	public void optionsOutputURIS() throws Exception{
		QName optName=Mock.ScriptGenerator.getOptionOutputDirName(0);
		ResultSetFactory.collectOptions(script,input,mapper,builder);
		ResultSet rSet=builder.build();
		List<JobResult> jobs=Lists.newLinkedList(rSet.getResults(optName));
		HashSet<URI> uris= new HashSet<URI>();
		uris.add(mapper.mapOutput(URI.create(dir+"dos.xml")));
		uris.add(mapper.mapOutput(URI.create(dir+"uno.xml")));
		uris.add(mapper.mapOutput(URI.create(dir+"tres.xml")));

		Assert.assertTrue(uris.contains(jobs.get(0).getPath()));
		Assert.assertTrue(uris.contains(jobs.get(1).getPath()));
		Assert.assertTrue(uris.contains(jobs.get(2).getPath()));

		
	}

	@Test
	public void optionsOutputIdx() throws Exception{
		QName optName=Mock.ScriptGenerator.getOptionOutputDirName(0);
		ResultSetFactory.collectOptions(script,input,mapper,builder);
		ResultSet rSet=builder.build();
		List<JobResult> jobs=Lists.newLinkedList(rSet.getResults(optName));
		HashSet<String> uris= new HashSet<String>();
		uris.add(dir+"dos.xml");
		uris.add(dir+"uno.xml");
		uris.add(dir+"tres.xml");

		Assert.assertTrue(uris.contains(jobs.get(0).getIdx()));
		Assert.assertTrue(uris.contains(jobs.get(1).getIdx()));
		Assert.assertTrue(uris.contains(jobs.get(2).getIdx()));

		
	}

	@Test
	public void newResultSet() throws Exception{
		String outName = Mock.ScriptGenerator.getOutputName(0);
		Provider<Result> res=output.getResultProvider(outName);
		res.provide();
		AbstractJobContext ctxt= new AbstractJobContext(JobIdFactory.newId(),script,input,output,mapper){};
		ResultSet rSet=ResultSetFactory.newResultSet(ctxt,mapper);
		Assert.assertEquals(5,rSet.getResults().size());
	}
}
