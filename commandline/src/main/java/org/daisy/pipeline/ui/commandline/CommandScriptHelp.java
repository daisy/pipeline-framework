package org.daisy.pipeline.ui.commandline;

import org.daisy.common.xproc.XProcOptionInfo;
import org.daisy.common.xproc.XProcPortInfo;
import org.daisy.pipeline.script.ScriptRegistry;
import org.daisy.pipeline.script.XProcOptionMetadata;
import org.daisy.pipeline.script.XProcPortMetadata;
import org.daisy.pipeline.script.XProcScript;
import org.daisy.pipeline.script.XProcScriptService;

public final class CommandScriptHelp implements Command {

	public static CommandScriptHelp newInstance(String scriptName,
			ScriptRegistry scriptRegistry) {
		return new CommandScriptHelp(scriptName, scriptRegistry);
	}

	private static String INDENT = "  ";

	private final String scriptName;
	private final ScriptRegistry scriptRegistry;

	private CommandScriptHelp(String scriptName, ScriptRegistry scriptRegistry) {
		this.scriptName = scriptName;
		this.scriptRegistry = scriptRegistry;
	}

	@Override
	public void execute() throws IllegalArgumentException {
		for (XProcScriptService scriptService : scriptRegistry.getScripts()) {
			if (scriptService.getName().equals(scriptName)) {
				XProcScript script = scriptService.load();
				System.out.println(script.getName());
				System.out.println();
				System.out.println(INDENT + script.getDescription());
				System.out.println();
				System.out.println("URI: " + script.getURI());
				for (XProcPortInfo input : script.getXProcPipelineInfo()
						.getInputPorts()) {
					XProcPortMetadata metadata = script.getPortMetadata(input
							.getName());
					System.out.println(INDENT + "Input port '"
							+ input.getName() + "': " + metadata.getNiceName()
							+ " (" + metadata.getMediaType() + ")");
					System.out.println(INDENT + INDENT
							+ metadata.getDescription());
				}
				for (XProcPortInfo output : script.getXProcPipelineInfo()
						.getOutputPorts()) {
					XProcPortMetadata metadata = script.getPortMetadata(output
							.getName());
					System.out.println(INDENT + "Input port '"
							+ output.getName() + "': " + metadata.getNiceName()
							+ " (" + metadata.getMediaType() + ")");
					System.out.println(INDENT + INDENT
							+ metadata.getDescription());
				}
				for (String paramPort : script.getXProcPipelineInfo()
						.getParameterPorts()) {
					System.out.println(INDENT + "Parameter port '" + paramPort);
				}
				for (XProcOptionInfo option : script.getXProcPipelineInfo()
						.getOptions()) {
					XProcOptionMetadata metadata = script
							.getOptionMetadata(option.getName());
					System.out.println(INDENT + "Option '" + option.getName()
							+ "': " + metadata.getNiceName() + " ("
							+ (option.isRequired() ? "required" : "optional"));
					System.out.println(INDENT + INDENT
							+ metadata.getDescription());
				}
				return;
			}
		}
		throw new RuntimeException("Script '" + scriptName + "' not found");
	}

}
