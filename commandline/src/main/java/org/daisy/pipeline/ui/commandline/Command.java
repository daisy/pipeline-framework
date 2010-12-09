package org.daisy.pipeline.ui.commandline;

import java.util.Properties;

public abstract class Command {
	protected Properties mArgs;
	public Command(Properties args) {
		mArgs=args;
	} 
	public abstract void execute() throws IllegalArgumentException;
}
