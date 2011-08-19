package org.daisy.pipeline.ui.commandline;

import java.io.IOException;

import joptsimple.OptionParser;

public final class CommandUsage implements Command {

	public static Command newInstance(OptionParser parser) {
		return new CommandUsage(parser, null);
	}

	public static Command newInstance(OptionParser parser, String message) {
		return new CommandUsage(parser, message);
	}

	private final String message;
	private final OptionParser parser;

	private CommandUsage(OptionParser parser, String message) {
		if (parser == null) {
			throw new IllegalArgumentException();
		}
		this.parser = parser;
		this.message = message == null ? "" : message;
	}

	@Override
	public void execute() {
		try {
			if (!message.isEmpty()) {
				System.err.println(message);
			}
			parser.printHelpOn(System.err);
			System.err.println();
		} catch (IOException e) {
			throw new RuntimeException("unexpected", e);
		}

	}

}
