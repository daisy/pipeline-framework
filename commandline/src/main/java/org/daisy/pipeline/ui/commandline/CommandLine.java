package org.daisy.pipeline.ui.commandline;

import java.util.Properties;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.daisy.pipeline.ui.commandline.provider.ServiceProvider;

public class CommandLine {

	OptionParser mParser;
	ServiceProvider mProvider;
	

	public CommandLine(ServiceProvider provider) {
		mProvider=provider;
		mParser = new OptionParser();
		mParser.accepts("l", "List of available converters");
		mParser.accepts("x", "xproc file to execute").withRequiredArg();
		mParser.accepts("i", "list of input ports in the format portName1:file1,portName2:file2  (only with -x modifier)").withRequiredArg();
		mParser.accepts("o", "list of output ports in the format portName1:file1,portName2:file2").withRequiredArg();
		mParser.accepts("p", "list of parameters in the format port1:param1:value1,param1:param2:value2 (only with -x modifier)").withRequiredArg();
		mParser.accepts("t", "list of options in the format opt1:value1,opt2:value2 (only with -x modifier)").withRequiredArg();
		mParser.accepts("h",
				"Show this help or the help for the given converter")
				.withOptionalArg().ofType(String.class)
				.describedAs("converter");

	}

	public Command parse(String... args) {
		
		if(args==null){
			return getUnrecovreableError("The arguments are null exiting...");
		}
		OptionSet oSet=null;
		try{
			oSet = mParser.parse(args);
		}catch(joptsimple.OptionException oe){
			return getUsageWithError(oe.getLocalizedMessage());
		}
		
		
		//Properties commandArgs = new Properties();
		if(!checkBasicArgs(oSet)){
			return getUsage();
		}
		
		if(oSet.has("l")){
			return getListCommand();
		}else if(oSet.has("x")){
			return getPipelineCommand(oSet);
		}
		
		//at this point the only option left should be help
		return getUsage();
	}
	
	private Command getPipelineCommand(OptionSet oSet) {
		Properties commandArgs= new Properties();
		commandArgs.put(CommandPipeline.PROVIDER, mProvider);
		String inputs="";
		if(oSet.valueOf("i")!=null)
				inputs=oSet.valueOf("i").toString();
		String outputs="";
		if(oSet.valueOf("o")!=null)
			outputs=oSet.valueOf("o").toString();
		String params="";
		if(oSet.valueOf("p")!=null)
			params=oSet.valueOf("p").toString();
		String pipeline="";
		if(oSet.valueOf("x")!=null)
			pipeline=oSet.valueOf("x").toString();
		String options="";
		if(oSet.valueOf("t")!=null)
			options=oSet.valueOf("t").toString();
		commandArgs.setProperty(CommandPipeline.INPUT, inputs);
		commandArgs.setProperty(CommandPipeline.OUTPUT, outputs);
		commandArgs.setProperty(CommandPipeline.PARAMS, params);
		commandArgs.setProperty(CommandPipeline.OPTIONS, options);
		commandArgs.setProperty(CommandPipeline.PIPELINE, pipeline);
		
		return new CommandPipeline(commandArgs);
	}

	private Command getListCommand() {
		Properties commandArgs= new Properties();
		commandArgs.put(CommandList.PROVIDER, mProvider);
		return new CommandList(commandArgs);
	}

	public boolean checkBasicArgs(OptionSet oSet){
		return oSet.has("l")||oSet.has("h")||oSet.has("x");
	}

	public Command getUnrecovreableError(String msg){
		Properties commandArgs= new Properties();
		commandArgs.put(CommandUnrecoverableError.ERR, msg);
		return new CommandUnrecoverableError(commandArgs);
	}
	
	private Command getUsage(){
		Properties commandArgs= new Properties();
		commandArgs.put(CommandUsage.PARSER, mParser);
		return new CommandUsage(commandArgs);
	}
	private Command getUsageWithError(String err){
		Properties commandArgs= new Properties();
		commandArgs.put(CommandUsage.PARSER, mParser);
		commandArgs.put(CommandUsage.ERR, err);
		return new CommandUsage(commandArgs);
	}
}

