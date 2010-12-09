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
		}
		
		//at this point the only option left should be help
		return getUsage();
	}
	
	private Command getListCommand() {
		Properties commandArgs= new Properties();
		commandArgs.put(CommandList.PROVIDER, mProvider);
		return new CommandList(commandArgs);
	}

	public boolean checkBasicArgs(OptionSet oSet){
		return oSet.has("l")||oSet.has("h");
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
