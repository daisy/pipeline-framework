package org.daisy.pipeline.ui.commandline;

import java.io.IOException;

import joptsimple.OptionParser;


/**
 * The Class CommandUsage shows the detailed help for the command line interface.
 */
public final class CommandUsage implements Command {

	/**
	 * New instance.
	 *
	 * @param parser the parser
	 * @return the command
	 */
	public static Command newInstance(OptionParser parser) {
		return new CommandUsage(parser, null);
	}

	/**
	 * New instance with a message
	 *
	 * @param parser the parser
	 * @param message the message
	 * @return the command
	 */
	public static Command newInstance(OptionParser parser, String message) {
		return new CommandUsage(parser, message);
	}

	/** The message. */
	private final String message;

	/** The parser. */
	private final OptionParser parser;

	/**
	 * Instantiates a new command usage.
	 *
	 * @param parser the parser
	 * @param message the message
	 */
	private CommandUsage(OptionParser parser, String message) {
		if (parser == null) {
			throw new IllegalArgumentException();
		}
		this.parser = parser;
		this.message = message == null ? "" : message;
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.ui.commandline.Command#execute()
	 */
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
