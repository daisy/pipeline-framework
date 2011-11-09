package org.daisy.pipeline.ui.commandline;


// TODO: Auto-generated Javadoc
/**
 * Contract for CLI commands.
 */
public interface Command {
	
	/**
	 * Executes the command.
	 *
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public void execute() throws IllegalArgumentException;
}
