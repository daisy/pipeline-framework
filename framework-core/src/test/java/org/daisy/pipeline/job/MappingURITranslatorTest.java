package org.daisy.pipeline.job;

import java.io.File;
import java.io.IOException;

import java.net.URI;

import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import javax.xml.transform.Source;

import org.daisy.common.base.Provider;

import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcOptionInfo;

import org.daisy.pipeline.script.XProcScript;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

public class MappingURITranslatorTest   {
	
	String oldIoBase="";
	String myBase="";
	String testFile="dir/myfile.xml";
	String testFile2="dir/myfile2.xml";
	String testDir="dir";
	File tmpdir;
	@Before
	public void setUp() throws IOException {
		oldIoBase=System.getProperty(MappingURITranslator.ORG_DAISY_PIPELINE_IOBASE);	
		tmpdir= new File(System.getProperty("java.io.tmpdir"));
		System.setProperty(MappingURITranslator.ORG_DAISY_PIPELINE_IOBASE,tmpdir.toString());	
	}
	@After
	public void tearDown(){
		if(oldIoBase!=null)
			System.setProperty(MappingURITranslator.ORG_DAISY_PIPELINE_IOBASE,oldIoBase);	
	}

	@Test
	public void testResolveInputPorts() throws IOException {
		//inputs from the script definition
		XProcScript mscript= new Mock.ScriptGenerator.Builder().withInputs(1).build().generate();
		Provider<Source> srcProv= Mock.getSourceProvider(testFile);
		Provider<Source> srcProv2= Mock.getSourceProvider(testFile2);
		//adding a value to the input option
		String optName=Mock.ScriptGenerator.getInputName(0);
		XProcInput input = new XProcInput.Builder().
				withInput(optName, srcProv).withInput(optName, srcProv2).build();

		XProcInput.Builder builder = new XProcInput.Builder();
		JobId id=JobIdFactory.newId();
		MappingURITranslator trans=MappingURITranslator.from(id,mscript,null);
		trans.translateInputPorts(mscript,input,builder);

		XProcInput newInput = builder.build();
		List<Provider<Source>> providers = Lists.newLinkedList(newInput.getInputs(optName));
		URI res1 = URI.create(providers.get(0).provide().getSystemId());
		URI expected=URI.create(tmpdir.toURI().toString()+id.toString()+"/"+MappingURITranslator.IO_DATA_SUBDIR+"/"+testFile);
		Assert.assertEquals(res1,expected);

		URI res2 = URI.create(providers.get(1).provide().getSystemId());
		URI expected2=URI.create(tmpdir.toURI().toString()+id.toString()+"/"+MappingURITranslator.IO_DATA_SUBDIR+"/"+testFile2);
		Assert.assertEquals(res2,expected2);
	}

	@Test
	public void testResolveInputPortGenerated() throws IOException {
		//inputs from the script definition
		XProcScript mscript= new Mock.ScriptGenerator.Builder().withInputs(1).build().generate();
		Provider<Source> srcProv= Mock.getSourceProvider(null);
		Provider<Source> srcProv2= Mock.getSourceProvider(null);
		//adding a value to the input option
		String optName=Mock.ScriptGenerator.getInputName(0);
		XProcInput input = new XProcInput.Builder().
				withInput(optName, srcProv).withInput(optName, srcProv2).build();

		XProcInput.Builder builder = new XProcInput.Builder();
		JobId id=JobIdFactory.newId();
		MappingURITranslator trans=MappingURITranslator.from(id,mscript,null);
		trans.translateInputPorts(mscript,input,builder);

		XProcInput newInput = builder.build();
		List<Provider<Source>> providers = Lists.newLinkedList(newInput.getInputs(optName));
		URI res1 = URI.create(providers.get(0).provide().getSystemId());
		URI expected=URI.create(tmpdir.toURI().toString()+id.toString()+"/"+MappingURITranslator.IO_DATA_SUBDIR+"/"+optName+"-0.xml");
		Assert.assertEquals(res1,expected);

		URI res2 = URI.create(providers.get(1).provide().getSystemId());
		URI expected2=URI.create(tmpdir.toURI().toString()+id.toString()+"/"+MappingURITranslator.IO_DATA_SUBDIR+"/"+optName+"-1.xml");
		Assert.assertEquals(res2,expected2);

	}

	@Test(expected=RuntimeException.class)
	public void testResolveInputPortURIError() throws IOException {
		//inputs from the script definition
		XProcScript mscript= new Mock.ScriptGenerator.Builder().withInputs(1).build().generate();
		Provider<Source> srcProv= Mock.getSourceProvider("with space.xml");
		//adding a value to the input option
		String optName=Mock.ScriptGenerator.getInputName(0);
		XProcInput input = new XProcInput.Builder().
				withInput(optName, srcProv).build();

		XProcInput.Builder builder = new XProcInput.Builder();
		JobId id=JobIdFactory.newId();
		MappingURITranslator trans=MappingURITranslator.from(id,mscript,null);
		trans.translateInputPorts(mscript,input,builder);

	}
	@Test
	public void testResolveOptionsInput() throws IOException {
		//inputs from the script definition
		XProcScript mscript= new Mock.ScriptGenerator.Builder().withOptionInputs(1).build().generate();
		Collection<XProcOptionInfo> optionInfos = Lists.newLinkedList(mscript.getXProcPipelineInfo().getOptions());
		//adding a value to the input option
		QName optName=Mock.ScriptGenerator.getOptionInputName(0);
		XProcInput input = new XProcInput.Builder()
				.withOption(optName, testFile).build();

		XProcInput.Builder builder = new XProcInput.Builder();
		JobId id=JobIdFactory.newId();
		MappingURITranslator trans=MappingURITranslator.from(id,mscript,null);
		trans.translateInputOptions(optionInfos,input,builder);

		XProcInput newInput = builder.build();
		URI res1 = URI.create(newInput.getOptions().get(optName));
		URI expected=URI.create(tmpdir.toURI().toString()+id.toString()+"/"+MappingURITranslator.IO_DATA_SUBDIR+"/"+testFile);
		Assert.assertEquals(res1,expected);
	}
	@Test(expected=RuntimeException.class)
	public void testResolveOptionsInputURIError() throws IOException {
		//inputs from the script definition

		XProcScript script= new Mock.ScriptGenerator.Builder().withOptionInputs(1).build().generate();
		Collection<XProcOptionInfo> optionInfos = Lists.newLinkedList(script.getXProcPipelineInfo().getOptions());
		//spaces makes uris sad
		String testFile="dir/my file.xml";
		QName optName=Mock.ScriptGenerator.getOptionInputName(0);
		//adding a value to the input option
		XProcInput input = new XProcInput.Builder()
				.withOption(optName,testFile).build();

		XProcInput.Builder builder = new XProcInput.Builder();
		JobId id=JobIdFactory.newId();
		MappingURITranslator trans=MappingURITranslator.from(id,script,null);
		trans.translateInputOptions(optionInfos,input,builder);

	}

	@Test
	public void testResolveOptionsInputEmpty() throws IOException {
		//it should just ignore them, rite?

		XProcScript script= new Mock.ScriptGenerator.Builder().withOptionInputs(1).build().generate();
		Collection<XProcOptionInfo> optionInfos = Lists.newLinkedList(script.getXProcPipelineInfo().getOptions());
		//no settings for the input
		QName optName=Mock.ScriptGenerator.getOptionInputName(0);
		XProcInput input = new XProcInput.Builder().build();

		XProcInput.Builder builder = new XProcInput.Builder();
		JobId id=JobIdFactory.newId();
		MappingURITranslator trans=MappingURITranslator.from(id,script,null);
		trans.translateInputOptions(optionInfos,input,builder);

		XProcInput newInput = builder.build();

		Assert.assertNull(newInput.getOptions().get(optName));
	}

	@Test
	public void testResolveOptionsOutputsCopy() throws IOException {

		XProcScript script= new Mock.ScriptGenerator.Builder().withOptionOther(1).build().generate();
		Collection<XProcOptionInfo> optionInfos = Lists.newLinkedList(script.getXProcPipelineInfo().getOptions());
		QName optName=Mock.ScriptGenerator.getRegularOptionName(0);
		//adding a value to the input option
		XProcInput input = new XProcInput.Builder().withOption(optName,"cosa").build();

		XProcInput.Builder builder = new XProcInput.Builder();
		JobId id=JobIdFactory.newId();
		MappingURITranslator trans=MappingURITranslator.from(id,script,null);
		trans.copyOptions(optionInfos,input,builder);

		XProcInput newInput = builder.build();

		Assert.assertEquals("cosa",newInput.getOptions().get(optName));
	}

	@Test
	public void testResolveOptionsOutputsFile() throws IOException {

		XProcScript script= new Mock.ScriptGenerator.Builder().withOptionOutputsFile(1).build().generate();
		Collection<XProcOptionInfo> optionInfos = Lists.newLinkedList(script.getXProcPipelineInfo().getOptions());
		QName optName=Mock.ScriptGenerator.getOptionOutputFileName(0);
		//adding a value to the input option
		XProcInput input = new XProcInput.Builder().withOption(optName,testFile).build();

		XProcInput.Builder builder = new XProcInput.Builder();
		JobId id=JobIdFactory.newId();
		MappingURITranslator trans=MappingURITranslator.from(id,script,null);
		trans.translateOutputOptions(optionInfos,input,builder);

		XProcInput newInput = builder.build();

		URI expected=URI.create(tmpdir.toURI().toString()+id.toString()+"/"+MappingURITranslator.IO_OUTPUT_SUBDIR+"/"+testFile);
		URI reslut=URI.create( newInput.getOptions().get(optName) );
		Assert.assertEquals(expected,reslut);
	}
	@Test
	public void testResolveOptionsOutputsDir() throws IOException {

		XProcScript script= new Mock.ScriptGenerator.Builder().withOptionOutputsDir(1).build().generate();
		Collection<XProcOptionInfo> optionInfos = Lists.newLinkedList(script.getXProcPipelineInfo().getOptions());
		QName optName=Mock.ScriptGenerator.getOptionOutputDirName(0);
		//adding a value to the input option
		XProcInput input = new XProcInput.Builder().withOption(optName,testDir).build();
		XProcInput.Builder builder = new XProcInput.Builder();
		JobId id=JobIdFactory.newId();
		MappingURITranslator trans=MappingURITranslator.from(id,script,null);
		trans.translateOutputOptions(optionInfos,input,builder);

		XProcInput newInput = builder.build();

		URI expected=URI.create(tmpdir.toURI().toString()+id.toString()+"/"+MappingURITranslator.IO_OUTPUT_SUBDIR+"/"+testDir);
		URI reslut=URI.create( newInput.getOptions().get(optName) );
		Assert.assertEquals(expected,reslut);
	}

	@Test
	public void testResolveOptionsOutputsGeneratedFile() throws IOException {

		XProcScript script= new Mock.ScriptGenerator.Builder().withOptionOutputsFile(1).build().generate();
		Collection<XProcOptionInfo> optionInfos = Lists.newLinkedList(script.getXProcPipelineInfo().getOptions());
		QName optName=Mock.ScriptGenerator.getOptionOutputFileName(0);
		//adding a value to the input option
		XProcInput input = new XProcInput.Builder().withOption(optName,"").build();

		XProcInput.Builder builder = new XProcInput.Builder();
		JobId id=JobIdFactory.newId();
		MappingURITranslator trans=MappingURITranslator.from(id,script,null);
		trans.translateOutputOptions(optionInfos,input,builder);

		XProcInput newInput = builder.build();
		String generated=URITranslatorHelper.generateOptionOutput(script.getXProcPipelineInfo().getOption(optName),script);
		URI expected=URI.create(tmpdir.toURI().toString()+id.toString()+"/"+MappingURITranslator.IO_OUTPUT_SUBDIR+"/"+generated);
		URI reslut=URI.create( newInput.getOptions().get(optName) );
		Assert.assertEquals(expected,reslut);
	}

	@Test
	public void testResolveOptionsOutputsGeneratedDir() throws IOException {

		XProcScript script= new Mock.ScriptGenerator.Builder().withOptionOutputsDir(1).build().generate();
		Collection<XProcOptionInfo> optionInfos = Lists.newLinkedList(script.getXProcPipelineInfo().getOptions());
		QName optName=Mock.ScriptGenerator.getOptionOutputDirName(0);
		//adding a value to the input option
		XProcInput input = new XProcInput.Builder().withOption(optName,"").build();

		XProcInput.Builder builder = new XProcInput.Builder();
		JobId id=JobIdFactory.newId();
		MappingURITranslator trans=MappingURITranslator.from(id,script,null);
		trans.translateOutputOptions(optionInfos,input,builder);

		XProcInput newInput = builder.build();
		String generated=URITranslatorHelper.generateOptionOutput(script.getXProcPipelineInfo().getOption(optName),script);
		URI expected=URI.create(tmpdir.toURI().toString()+id.toString()+"/"+MappingURITranslator.IO_OUTPUT_SUBDIR+"/"+generated);
		URI reslut=URI.create( newInput.getOptions().get(optName) );
		Assert.assertEquals(expected,reslut);
	}

	@Test(expected=RuntimeException.class)
	public void testResolveOptionsOutputsURIError() throws IOException {

		XProcScript script= new Mock.ScriptGenerator.Builder().withOptionOutputsFile(1).build().generate();
		Collection<XProcOptionInfo> optionInfos = Lists.newLinkedList(script.getXProcPipelineInfo().getOptions());
		QName optName=Mock.ScriptGenerator.getOptionOutputFileName(0);
		//adding a value to the input option
		XProcInput input = new XProcInput.Builder().withOption(optName,"with space.xml").build();

		XProcInput.Builder builder = new XProcInput.Builder();
		JobId id=JobIdFactory.newId();
		MappingURITranslator trans=MappingURITranslator.from(id,script,null);
		trans.translateOutputOptions(optionInfos,input,builder);

		XProcInput newInput = builder.build();
		String generated=URITranslatorHelper.generateOptionOutput(script.getXProcPipelineInfo().getOption(optName),script);
	}

	/**
	 * Tests 'translateInputs'. The details are tested in the rest of the methods of this class/
	 * This test is just to check that we go through all the kind of options
	 *
	 * @see org.daisy.pipeline.job.MappingURITranslator#translateInputs(XProcInput)
	 */
	@Test
	public void translateInputs() throws Exception {
		XProcScript script= new Mock.ScriptGenerator.Builder().withOptionInputs(1).withOptionOther(1).withOptionOutputsDir(1).withOptionOutputsFile(1).build().generate();
		QName optIn      = Mock.ScriptGenerator.getOptionInputName(0);
		QName optReg     = Mock.ScriptGenerator.getRegularOptionName(0);
		QName optOutFile = Mock.ScriptGenerator.getOptionOutputFileName(0);
		QName optOutDir  = Mock.ScriptGenerator.getOptionOutputDirName(0);

		XProcInput input = new XProcInput.Builder()
			.withOption(optIn,"dir/input.xml")
			.withOption(optReg,"value")
			.withOption(optOutFile,"dir/output.xml")
			.withOption(optOutDir,"outs")
			.build();

		JobId id=JobIdFactory.newId();
		MappingURITranslator trans=MappingURITranslator.from(id,script,null);
		XProcInput iTrans=trans.translateInputs(input);

		Assert.assertEquals(iTrans.getOptions().get(optIn),tmpdir.toURI().toString()+id.toString()+"/"+MappingURITranslator.IO_DATA_SUBDIR+"/"+"dir/input.xml");
		Assert.assertEquals(iTrans.getOptions().get(optOutFile),tmpdir.toURI().toString()+id.toString()+"/"+MappingURITranslator.IO_OUTPUT_SUBDIR+"/"+"dir/output.xml");
		Assert.assertEquals(iTrans.getOptions().get(optOutDir),tmpdir.toURI().toString()+id.toString()+"/"+MappingURITranslator.IO_OUTPUT_SUBDIR+"/"+"outs");
		Assert.assertEquals(iTrans.getOptions().get(optReg),"value");

	}

}
