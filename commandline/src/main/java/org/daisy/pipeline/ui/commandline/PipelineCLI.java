package org.daisy.pipeline.ui.commandline;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.daisy.common.xproc.XProcEngine;
import org.daisy.pipeline.modules.ModuleRegistry;
import org.daisy.pipeline.script.ScriptRegistry;
import org.daisy.pipeline.script.XProcScriptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PipelineCLI {

	private static final Logger logger = LoggerFactory
			.getLogger(PipelineCLI.class);

	public static final String MODE_PROPERTY = "org.daisy.pipeline.mode";
	private static final String CMD = "cmd";
	private static boolean EXIT = false;

	private final OptionParser parser;
	private ModuleRegistry moduleRegistry;
	private ScriptRegistry scriptRegistry;
	private XProcEngine xprocEngine;

	public PipelineCLI() {
		parser = new OptionParser();
		parser.accepts("l", "list of available uris");
		parser.accepts("s",
				"list of available scripts or if a script name is present it will be executed")
				.withOptionalArg().ofType(String.class)
				.describedAs("script name");
		parser.accepts("x", "xproc file to execute").withRequiredArg()
				.describedAs("XProc document");
		parser.accepts("i", "list of input ports").withRequiredArg()
				.describedAs("portName1=file1,...");
		parser.accepts("o", "list of output ports").withRequiredArg()
				.describedAs("portName1=file1,...");
		parser.accepts("p", "list of parameters").withRequiredArg()
				.describedAs("port1=param1=value1,...");
		parser.accepts("t", "list of options").withRequiredArg()
				.describedAs("opt1=value1,...");
		parser.accepts("h",
				"show this help or the help for the given converter")
				.withOptionalArg().ofType(String.class)
				.describedAs("converter");

	}

	public void activate() {
		// TODO move MODE_PROPERTY constant
		if (System.getProperty(MODE_PROPERTY) != null
				&& System.getProperty(MODE_PROPERTY).equals(CMD)) {
			logger.info("starting cmd");
			new Thread() {
				public void run() {
					String args = System
							.getProperty("org.daisy.pipeline.cmdargs");
					// awful getevn thanks to the disappointing pax runner --vmo
					// space support
					// TODO still needed ?
					if (args == null) {
						args = System.getenv("DAISY_ARGS");
					}
					if (args == null) {
						getUnrecovreableError("The arguments are null")
								.execute();
						if (EXIT) {
							System.exit(1);
						}
					} else {

						try {
							parse(args.split("\\s")).execute();
						} catch (Exception e) {
							getUnrecovreableError(e.getMessage()).execute();
							logger.error(e.getMessage(), e);
							if (EXIT) {
								System.exit(1);
							}
						}
						if (EXIT) {
							System.exit(0);
						}
					}

				}
			}.start();
		}
	}

	public void setModuleRegistry(ModuleRegistry moduleRegistry) {
		this.moduleRegistry = moduleRegistry;
	}

	public void setScriptRegistry(ScriptRegistry scriptRegistry) {
		this.scriptRegistry = scriptRegistry;
	}

	public void setXProcEngine(XProcEngine xprocEngine) {
		this.xprocEngine = xprocEngine;
	}

	public Command parse(String... args) {

		if (args == null) {
			return getUnrecovreableError("The arguments are null exiting...");
		}
		OptionSet oSet = null;
		try {
			oSet = parser.parse(args);
		} catch (OptionException oe) {
			return getUsageWithError(oe.getLocalizedMessage());
		}

		if (oSet.has("l")) {
			return getListURIsCommand();
		} else if (oSet.has("s")) {
			return getScriptCommand(oSet);
		} else if (oSet.has("x")) {
			return getPipelineCommand(oSet);
		} else if (oSet.has("h")) {
			return getHelpCommand(oSet);
		}

		// at this point the only option left should be help
		return getUsage();
	}

	private Command getHelpCommand(OptionSet oSet) {
		if (oSet.valueOf("h") != null
				&& !oSet.valueOf("h").toString().isEmpty()) {
			return CommandScriptHelp.newInstance(oSet.valueOf("h").toString(),
					scriptRegistry);
		} else {
			return getUsage();
		}
	}

	private Command getPipelineCommand(OptionSet oSet) {
		String inputs = "";
		if (oSet.valueOf("i") != null)
			inputs = oSet.valueOf("i").toString();
		String outputs = "";
		if (oSet.valueOf("o") != null)
			outputs = oSet.valueOf("o").toString();
		String params = "";
		if (oSet.valueOf("p") != null)
			params = oSet.valueOf("p").toString();
		String pipeline = "";
		if (oSet.valueOf("x") != null)
			pipeline = oSet.valueOf("x").toString();
		String options = "";
		if (oSet.valueOf("t") != null)
			options = oSet.valueOf("t").toString();
		return CommandPipeline.newInstance(pipeline, inputs, outputs, params,
				options, xprocEngine);
	}

	private Command getScriptCommand(OptionSet oSet) {
		if (oSet.valueOf("s") == null || oSet.valueOf("s").toString().isEmpty()) {
			return CommandListScripts.newInstance(scriptRegistry);
		} else {
			String scriptName = oSet.valueOf("s").toString();
			for (XProcScriptService scriptService : scriptRegistry.getScripts()) {
				if (scriptService.getName().equals(scriptName)) {
					String inputs = "";
					if (oSet.valueOf("i") != null)
						inputs = oSet.valueOf("i").toString();
					String outputs = "";
					if (oSet.valueOf("o") != null)
						outputs = oSet.valueOf("o").toString();
					String params = "";
					if (oSet.valueOf("p") != null)
						params = oSet.valueOf("p").toString();
					String options = "";
					if (oSet.valueOf("t") != null)
						options = oSet.valueOf("t").toString();
					return CommandPipeline.newInstance(scriptService.getURI()
							.toString(), inputs, outputs, params, options,
							xprocEngine);
				}
			}
			return getUsageWithError("Script '" + scriptName + "' not found");
		}
	}

	private Command getListURIsCommand() {
		return CommandListURIs.newInstance(moduleRegistry);
	}

	private Command getUnrecovreableError(String msg) {
		return CommandUnrecoverableError.newInstance(msg);
	}

	private Command getUsage() {
		return CommandUsage.newInstance(parser);
	}

	private Command getUsageWithError(String err) {
		return CommandUsage.newInstance(parser, err);
	}

}
