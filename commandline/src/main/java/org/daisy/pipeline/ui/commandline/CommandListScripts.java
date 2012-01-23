package org.daisy.pipeline.ui.commandline;

import org.daisy.pipeline.script.ScriptRegistry;
import org.daisy.pipeline.script.XProcScriptService;



/**
 * CommandListScripts lists the available scripts.
 */
public final class CommandListScripts implements Command {

	/** The script registry. */
	private final ScriptRegistry scriptRegistry;

	/**
	 * New instance.
	 *
	 * @param scriptRegistry the script registry
	 * @return the command list scripts
	 */
	public static CommandListScripts newInstance(ScriptRegistry scriptRegistry){
		return new CommandListScripts(scriptRegistry);
	}

	/**
	 * Instantiates a new command list scripts.
	 *
	 * @param scriptRegistry the script registry
	 */
	private CommandListScripts(ScriptRegistry scriptRegistry) {
		this.scriptRegistry = scriptRegistry;
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.ui.commandline.Command#execute()
	 */
	@Override
	public void execute() throws IllegalArgumentException {
		for (XProcScriptService script : scriptRegistry.getScripts()) {
			System.out.println(script.getName()+" - "+script.getDescription());
		}
	}

}
