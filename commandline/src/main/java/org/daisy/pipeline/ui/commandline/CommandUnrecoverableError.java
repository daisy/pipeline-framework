package org.daisy.pipeline.ui.commandline;


public final class CommandUnrecoverableError implements Command {

	public static Command newInstance(String message) {
		return new CommandUnrecoverableError(message == null ? "Unknown"
				: message);
	}

	private final String message;

	private CommandUnrecoverableError(String message) {
		this.message = message;
	}

	@Override
	public void execute() throws IllegalArgumentException {
		System.err.println("Unrecoverable error: " + message);

	}

}
