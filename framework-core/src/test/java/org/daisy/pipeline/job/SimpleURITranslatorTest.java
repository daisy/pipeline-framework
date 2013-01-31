package org.daisy.pipeline.job;

import javax.xml.namespace.QName;

import org.daisy.common.base.Provider;

import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcOutput;

import javax.xml.transform.Result;
import org.daisy.pipeline.script.XProcScript;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SimpleURITranslatorTest   {


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

		SimpleURITranslator trans=SimpleURITranslator.from(script);
		XProcInput iTrans=trans.translateInputs(input);

		Assert.assertEquals(iTrans.getOptions().get(optIn)      ,"dir/input.xml");
		Assert.assertEquals(iTrans.getOptions().get(optOutFile) ,"dir/output.xml");
		Assert.assertEquals(iTrans.getOptions().get(optOutDir)  ,"outs");
		Assert.assertEquals(iTrans.getOptions().get(optReg)     , "value");

	}

	@Test 
	public void ouputPortFile() throws Exception{
		XProcScript script= new Mock.ScriptGenerator.Builder().withOutputPorts(1).build().generate();
		String outName = Mock.ScriptGenerator.getOutputName(0);

		XProcOutput outs = new XProcOutput.Builder().withOutput(outName,Mock.getResultProvider("dir/file.xml")).build();
		SimpleURITranslator trans=SimpleURITranslator.from(script);
		XProcOutput translated=trans.translateOutput(outs);
		
		Provider<Result> res=translated.getResultProvider(outName);
		String expected=("dir/file.xml");
		
		Assert.assertEquals(expected.toString(),res.provide().getSystemId());


	}

	@Test 
	public void ouputSeqPortFiles() throws Exception{
		XProcScript script= new Mock.ScriptGenerator.Builder().withOutputPorts(1).build().generate();
		String outName = Mock.ScriptGenerator.getOutputName(0);

		XProcOutput outs = new XProcOutput.Builder().withOutput(outName,Mock.getResultProvider("dir/file.xml")).build();
		SimpleURITranslator trans=SimpleURITranslator.from(script);
		XProcOutput translated=trans.translateOutput(outs);
		
		Provider<Result> res=translated.getResultProvider(outName);
		String expected2=("dir/file-1.xml");
		
		//Assert.assertEquals(expected.toString(),res.provide().getSystemId());
		//discard one
		res.provide();
		Assert.assertEquals(expected2.toString(),res.provide().getSystemId());

	}

	@Test 
	public void ouputPortDir() throws Exception{
		XProcScript script= new Mock.ScriptGenerator.Builder().withOutputPorts(1).build().generate();
		String outName = Mock.ScriptGenerator.getOutputName(0);

		XProcOutput outs = new XProcOutput.Builder().withOutput(outName,Mock.getResultProvider("dir/")).build();
		SimpleURITranslator trans=SimpleURITranslator.from(script);
		XProcOutput translated=trans.translateOutput(outs);
		
		Provider<Result> res=translated.getResultProvider(outName);
		String expected=("dir/"+outName+".xml");
		
		Assert.assertEquals(expected.toString(),res.provide().getSystemId());
	}

	@Test 
	public void ouputSeqPortDir() throws Exception{
		XProcScript script= new Mock.ScriptGenerator.Builder().withOutputPorts(1).build().generate();
		String outName = Mock.ScriptGenerator.getOutputName(0);

		XProcOutput outs = new XProcOutput.Builder().withOutput(outName,Mock.getResultProvider("dir/")).build();
		SimpleURITranslator trans=SimpleURITranslator.from(script);
		XProcOutput translated=trans.translateOutput(outs);
		
		Provider<Result> res=translated.getResultProvider(outName);
		String expected=("dir/"+outName+"-1.xml");
		//discard first
		res.provide();
		Assert.assertEquals(expected.toString(),res.provide().getSystemId());
	}

	@Test 
	public void ouputPortEmptyString() throws Exception{
		XProcScript script= new Mock.ScriptGenerator.Builder().withOutputPorts(1).build().generate();
		String outName = Mock.ScriptGenerator.getOutputName(0);

		XProcOutput outs = new XProcOutput.Builder().withOutput(outName,Mock.getResultProvider("")).build();
		SimpleURITranslator trans=SimpleURITranslator.from(script);
		XProcOutput translated=trans.translateOutput(outs);
		
		Provider<Result> res=translated.getResultProvider(outName);
		String expected="";
		
		Assert.assertEquals(expected.toString(),res.provide().getSystemId());
	}

	@Test 
	public void ouputPortEmptyNull() throws Exception{
		XProcScript script= new Mock.ScriptGenerator.Builder().withOutputPorts(1).build().generate();
		String outName = Mock.ScriptGenerator.getOutputName(0);

		XProcOutput outs = new XProcOutput.Builder().build();
		SimpleURITranslator trans=SimpleURITranslator.from(script);
		XProcOutput translated=trans.translateOutput(outs);
		
		Provider<Result> res=translated.getResultProvider(outName);
		String expected="";
		
		Assert.assertEquals(expected.toString(),res.provide().getSystemId());
	}

		
}
