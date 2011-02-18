package org.daisy.pipeline.ui.commandline;

import java.util.Properties;

public class CommandUnrecoverableError extends Command {
	public static final String ERR = "ERR";

	public CommandUnrecoverableError(Properties args) {
		super(args);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() throws IllegalArgumentException {
		System.err.print("Unrecoverable error:");
		String err = "Unknown";
		if(mArgs.containsKey(ERR)){
			err =mArgs.getProperty(ERR);
		}
		System.err.println(" "+err);
		
	}

}
