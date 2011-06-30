package org.daisy.pipeline.ui.commandline;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandUnrecoverableError extends Command {
	public static final String ERR = "ERR";
	private Logger mLogger = LoggerFactory.getLogger(getClass().getCanonicalName());
	public CommandUnrecoverableError(Properties args) {
		super(args);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() throws IllegalArgumentException {
		//System.err.print("Unrecoverable error:");
		String err = "Unknown";
		if(mArgs.containsKey(ERR)){
			err =mArgs.getProperty(ERR);
		}
		System.err.println("Unrecoverable error:"+err);
		mLogger.error("Unrecoverable error: "+err);
		mLogger.warn("Shutting down due to previous errors");
		
	}

}
