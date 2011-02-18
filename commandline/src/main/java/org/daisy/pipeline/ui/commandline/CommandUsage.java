package org.daisy.pipeline.ui.commandline;

import java.io.IOException;
import java.util.Properties;

import joptsimple.OptionParser;

public class CommandUsage extends Command {

	public static final String PARSER = "parser";
	public static final String ERR = "ERR";

	private String mError = "";

	public CommandUsage(Properties args) {
		super(args);

	}

	@Override
	public void execute() {
		if (!mArgs.containsKey(PARSER)) {
			throw new IllegalArgumentException("Expecting parser as argument");
		}
		OptionParser parser = null;
		try {
			parser = (OptionParser) mArgs.get(PARSER);
		} catch (ClassCastException cce) {
			throw new IllegalArgumentException(
					"Parser arg is not an option parser");
		}

		if (mArgs.containsKey(ERR)) {
			mError = (String) mArgs.getProperty(ERR);
		}
		if(mError==null)
			throw new IllegalArgumentException("The error msg is not a string or is null");

		try {
			if (!getError().isEmpty()) {
				System.err.println(getError());
			}
			parser.printHelpOn(System.err);
			System.err.println();
		} catch (IOException e) {
			throw new IllegalArgumentException(
					"Error writing help, this should never happen");
		}

	}

	public String getError() {
		return mError;
	}

}
