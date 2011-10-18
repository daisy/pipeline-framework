package org.daisy.pipeline.ui.commandline;



/**
 * CommandUnrecoverableError is shown when something went really bad.
 */
public final class CommandUnrecoverableError implements Command {

	/**
	 * New instance with the error message.
	 *
	 * @param message the message
	 * @return the command
	 */
	public static Command newInstance(String message) {
		return new CommandUnrecoverableError(message == null ? "Unknown"
				: message);
	}

	/** The message. */
	private final String message;

	/**
	 * Instantiates a new command unrecoverable error.
	 *
	 * @param message the message
	 */
	private CommandUnrecoverableError(String message) {
		this.message = message;
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.ui.commandline.Command#execute()
	 */
	@Override
	public void execute() throws IllegalArgumentException {
		System.err.println("Unrecoverable error: " + message);

	}

}
