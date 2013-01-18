package org.daisy.pipeline.job;

import java.io.File;
import java.io.IOException;

import java.net.URI;

import java.util.Collection;
import java.util.HashMap;

import javax.xml.namespace.QName;

import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcOptionInfo;
import org.daisy.common.xproc.XProcPipelineInfo;
import org.daisy.common.xproc.XProcPortInfo;

import org.daisy.pipeline.script.XProcOptionMetadata;
import org.daisy.pipeline.script.XProcScript;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Predicates;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

public class RemoteURITranslatorTest   {
	
	XProcScript script;
	String oldIoBase="";
	String myBase="";
	File tmpdir;
	@Before
	public void setUp() throws IOException {
		oldIoBase=System.getProperty(RemoteURITranslator.ORG_DAISY_PIPELINE_IOBASE);	
		tmpdir= new File(System.getProperty("java.io.tmpdir"));
		System.setProperty(RemoteURITranslator.ORG_DAISY_PIPELINE_IOBASE,tmpdir.toString());	
		XProcPortInfo portInf = XProcPortInfo.newInputPort("source", false,
				true);
		//input select
		XProcOptionInfo optionInfInput = XProcOptionInfo.newOption(new QName(
				"myinput"), false, "");

		//empty input select
		XProcOptionInfo optionInfInput2 = XProcOptionInfo.newOption(new QName(
				"myEmptyInput"), false, "");

		XProcOptionInfo optionInf2 = XProcOptionInfo.newOption(new QName(
				"myopt"), false, "");
		//output no select
		XProcOptionInfo optionInf3 = XProcOptionInfo.newOption(
				new QName("out1"), false, "");
		//output no select
		XProcOptionInfo optionInf4 = XProcOptionInfo.newOption(
				new QName("out2"), false, "");
		//output with select
		XProcOptionInfo optionInf5 = XProcOptionInfo.newOption(
				new QName("out3"), false, "cosa");

		XProcPipelineInfo info = new XProcPipelineInfo.Builder()
				.withPort(portInf).withURI(URI.create(""))
				.withOption(optionInfInput).withOption(optionInf2).withOption(optionInf3).
				withOption(optionInf4).withOption(optionInf5).withOption(optionInfInput2).build();
		XProcOptionMetadata meta1 = new XProcOptionMetadata.Builder()
				.withType("anyFileURI").build();

		XProcOptionMetadata metaInput2 = new XProcOptionMetadata.Builder()
				.withType("anyFileURI").build();
				//.withDirection("input").withType("anyFileURI").build();
		XProcOptionMetadata meta2 = new XProcOptionMetadata.Builder()
				.withOutput("result").withType("anyFileURI").build();
				//.withDirection("output").withType("anyFileURI").build();

		XProcOptionMetadata meta3 = new XProcOptionMetadata.Builder()
				.withOutput("result").withType("anyFileURI").build();
				//.withDirection("output").withType("anyFileURI").build();

		XProcOptionMetadata meta4 = new XProcOptionMetadata.Builder()
				.withOutput("result").withType("anyDirURI").build();
				//.withDirection("output").withType("anyDirURI").build();

		XProcOptionMetadata meta5 = new XProcOptionMetadata.Builder()
				.withOutput("result").withType("anyDirURI").build();
				//.withDirection("output").withType("anyDirURI").build();

		HashMap<QName, XProcOptionMetadata> ometas = new HashMap<QName, XProcOptionMetadata>();
		ometas.put(new QName("myinput"), meta1);
		ometas.put(new QName("myEmptyInput"), metaInput2);
		ometas.put(new QName("myopt"), meta2);
		ometas.put(new QName("out1"), meta3);
		ometas.put(new QName("out2"), meta4);
		ometas.put(new QName("out3"), meta5);
		script = new XProcScript(info, null, null, null, null, ometas,null);

	}
	@After
	public void tearDown(){
		if(oldIoBase!=null)
			System.setProperty(RemoteURITranslator.ORG_DAISY_PIPELINE_IOBASE,oldIoBase);	
	}

	@Test
	public void testResolveOptionsInput() throws IOException {
		//inputs from the script definition

		Collection<XProcOptionInfo> optionInfos = Lists.newLinkedList(script.getXProcPipelineInfo().getOptions());
		Collection<XProcOptionInfo> inputs= Collections2.filter(optionInfos,Predicates.and(URITranslatorHelper.getTranslatableOptionFilter(script),
					Predicates.not(URITranslatorHelper.getOutputOptionFilter(script))));
		String testFile="dir/myfile.xml";
		//adding a value to the input option
		XProcInput input = new XProcInput.Builder()
				.withOption(new QName("myinput"), testFile).build();

		XProcInput.Builder builder = new XProcInput.Builder();
		JobId id=JobIdFactory.newId();
		RemoteURITranslator trans=RemoteURITranslator.from(id,script,null);
		trans.translateInputOptions(inputs,input,builder);

		XProcInput newInput = builder.build();
		URI res1 = URI.create(newInput.getOptions().get(new QName("myinput")));
		URI expected=URI.create(tmpdir.toURI().toString()+id.toString()+"/"+RemoteURITranslator.IO_DATA_SUBDIR+"/"+testFile);
		Assert.assertEquals(res1,expected);
	}

	@Test
	public void testResolveOptionsInputEmpty() throws IOException {
		//it should just ignore them, rite?

		Collection<XProcOptionInfo> optionInfos = Lists.newLinkedList(script.getXProcPipelineInfo().getOptions());
		Collection<XProcOptionInfo> inputs= Collections2.filter(optionInfos,Predicates.and(URITranslatorHelper.getTranslatableOptionFilter(script),
					Predicates.not(URITranslatorHelper.getOutputOptionFilter(script))));
		String testFile="dir/myfile.xml";
		//adding a value to the input option
		XProcInput input = new XProcInput.Builder().build();

		XProcInput.Builder builder = new XProcInput.Builder();
		JobId id=JobIdFactory.newId();
		RemoteURITranslator trans=RemoteURITranslator.from(id,script,null);
		trans.translateInputOptions(inputs,input,builder);

		XProcInput newInput = builder.build();

		Assert.assertNull(newInput.getOptions().get(new QName("myinput")));
		Assert.assertNull(newInput.getOptions().get(new QName("myEmptyInput")));
	}
}
