package org.daisy.pipeline.ui.commandline;

import org.daisy.pipeline.script.ScriptRegistry;
import org.daisy.pipeline.script.XProcScriptService;


public final class CommandListScripts implements Command {

	private final ScriptRegistry scriptRegistry;
	
	public static CommandListScripts newInstance(ScriptRegistry scriptRegistry){
		return new CommandListScripts(scriptRegistry);
	}
	
	private CommandListScripts(ScriptRegistry scriptRegistry) {
		this.scriptRegistry = scriptRegistry;
	}

	@Override
	public void execute() throws IllegalArgumentException {
		for (XProcScriptService script : scriptRegistry.getScripts()) {
			System.out.println(script.getName()+" - "+script.getDescription());
		}
	}

}
