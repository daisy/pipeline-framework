package org.daisy.pipeline.test.ui.commandline;

import java.util.HashMap;
import java.util.Properties;

import junit.framework.Assert;

import org.daisy.pipeline.ui.commandline.CommandPipeline;
import org.junit.Test;


public class CommandPipelineTest {
	@Test
	public void parseInputTest(){
		String list="a:b";
		HashMap<String, String > pairs= new CommandPipeline(null).parseInputList(list);
		Assert.assertTrue(pairs.containsKey("a"));
		Assert.assertEquals("b",pairs.get("a"));
		list="a:b,c:d";
		pairs= new CommandPipeline(null).parseInputList(list);
		Assert.assertTrue(pairs.containsKey("a"));
		Assert.assertEquals("b",pairs.get("a"));
		Assert.assertTrue(pairs.containsKey("c"));
		Assert.assertEquals("d",pairs.get("c"));
		list="";
		pairs= new CommandPipeline(null).parseInputList(list);
		//no error should be thrown
	}
	@Test
	public void parseParamsTest(){
		String list="a:b:c";
		HashMap<String, HashMap<String, String> > pairs= new CommandPipeline(null).parseParamsList(list);
		Assert.assertTrue(pairs.containsKey("a"));
		Assert.assertTrue(pairs.get("a").containsKey("b"));
		Assert.assertEquals("c",pairs.get("a").get("b"));
		list="a:b:c,c:d:e";
		pairs= new CommandPipeline(null).parseParamsList(list);
		Assert.assertTrue(pairs.containsKey("a"));
		Assert.assertTrue(pairs.get("a").containsKey("b"));
		Assert.assertEquals("c",pairs.get("a").get("b"));
		Assert.assertTrue(pairs.containsKey("c"));
		Assert.assertTrue(pairs.get("c").containsKey("d"));
		Assert.assertEquals("e",pairs.get("c").get("d"));
		list="";
		pairs= new CommandPipeline(null).parseParamsList(list);
		//no error should be thrown
	}
	@Test
	public void parseArgsTestErrors(){
		String list="a:";
		try{
			HashMap<String, HashMap<String, String> >  pairs= new CommandPipeline(null).parseParamsList(list);
			Assert.fail("Expecting error in parsing list");
		}catch (Exception e) {
			
		}
		list="a:b:,:c:";
		try{
			HashMap<String, HashMap<String, String> >  pairs= new CommandPipeline(null).parseParamsList(list);
			Assert.fail("Expecting error in parsing list");
		}catch (Exception e) {
			
		}	
	}
	
	@Test
	public void parseInputTestErrors(){
		String list="a:";
		try{
			HashMap<String, String > pairs= new CommandPipeline(null).parseInputList(list);
			Assert.fail("Expecting error in parsing list");
		}catch (Exception e) {
			
		}
		list="a:b,:c";
		try{
			HashMap<String, String > pairs= new CommandPipeline(null).parseInputList(list);
			Assert.fail("Expecting error in parsing list");
		}catch (Exception e) {
			
		}	
	}
	
	
	@Test
	public void noPipeline(){
		Properties props = new Properties();
				
		props.setProperty(CommandPipeline.INPUT, "source:tralara/trulrlerler");
		props.setProperty(CommandPipeline.PROVIDER, "");
		props.setProperty(CommandPipeline.OUTPUT, "result:tralara/trulrlerler");
		try{
			new CommandPipeline(props).execute();
			Assert.fail("Expecting source error ");
		}catch(Exception e){
			
		}
	}
	
	
	
	@Test
	public void fileNotFound(){
		Properties props = new Properties();
		props.setProperty("result", "tralari");
		props.setProperty(CommandPipeline.PIPELINE, "pipe");
		props.setProperty(CommandPipeline.INPUT, "source:tralara/trulrlerler");
		props.setProperty(CommandPipeline.PROVIDER, "");
		props.setProperty(CommandPipeline.OUTPUT, "result:tralara/trulrlerler");
		try{
			new CommandPipeline(props).execute();
			Assert.fail("Expecting source error ");
		}catch(Exception e){
			
		}
	}
	
	
}
