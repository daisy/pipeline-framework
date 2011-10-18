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


/**
 *  CLI client 
 */
public class PipelineCLI {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory
			.getLogger(PipelineCLI.class);

	/** The Constant MODE_PROPERTY. */
	public static final String MODE_PROPERTY = "org.daisy.pipeline.mode";
	
	/** The Constant CMD. */
	private static final String CMD = "cmd";
	
	/** The EXIT. */
	private static boolean EXIT = true;
	
	/** The parser. */
	private final OptionParser parser;
	
	/** The module registry. */
	private ModuleRegistry moduleRegistry;
	
	/** The script registry. */
	private ScriptRegistry scriptRegistry;
	
	/** The xproc engine. */
	private XProcEngine xprocEngine;

	/**
	 * Instantiates a new pipeline cli.
	 */
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

	/**
	 * Activate (OSGI)
	 */
	public void activate() {
		logger.trace("Activating CLI");
		// TODO move MODE_PROPERTY constant
		if (System.getProperty(MODE_PROPERTY) != null
				&& System.getProperty(MODE_PROPERTY).equals(CMD)) {
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
	
	/**
	 * Deactivates the cli
	 *
	 * @param reason the reason
	 */
	public void deactivate(int reason){
			LoggerFactory.getLogger("tracer").trace("Deactivating {} {}",this.getClass().getSimpleName(),reason);
	}

	/**
	 * Sets the module registry.
	 *
	 * @param moduleRegistry the new module registry
	 */
	public void setModuleRegistry(ModuleRegistry moduleRegistry) {
		this.moduleRegistry = moduleRegistry;
	}

	/**
	 * Sets the script registry.
	 *
	 * @param scriptRegistry the new script registry
	 */
	public void setScriptRegistry(ScriptRegistry scriptRegistry) {
		this.scriptRegistry = scriptRegistry;
	}

	/**
	 * Sets the x proc engine.
	 *
	 * @param xprocEngine the new x proc engine
	 */
	public void setXProcEngine(XProcEngine xprocEngine) {
		this.xprocEngine = xprocEngine;
	}

	/**
	 * Parses the argument list from the command line.
	 *
	 * @param args the args
	 * @return the command
	 */
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

	/**
	 * Gets the help command.
	 *
	 * @param oSet the o set
	 * @return the help command
	 */
	private Command getHelpCommand(OptionSet oSet) {
		if (oSet.valueOf("h") != null
				&& !oSet.valueOf("h").toString().isEmpty()) {
			return CommandScriptHelp.newInstance(oSet.valueOf("h").toString(),
					scriptRegistry);
		} else {
			return getUsage();
		}
	}

	/**
	 * Gets the pipeline command.
	 *
	 * @param oSet the o set
	 * @return the pipeline command
	 */
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

	/**
	 * Gets the script command.
	 *
	 * @param oSet the o set
	 * @return the script command
	 */
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

	/**
	 * Gets the list ur is command.
	 *
	 * @return the list ur is command
	 */
	private Command getListURIsCommand() {
		return CommandListURIs.newInstance(moduleRegistry);
	}

	/**
	 * Gets the unrecovreable error.
	 *
	 * @param msg the msg
	 * @return the unrecovreable error
	 */
	private Command getUnrecovreableError(String msg) {
		return CommandUnrecoverableError.newInstance(msg);
	}

	/**
	 * Gets the usage.
	 *
	 * @return the usage
	 */
	private Command getUsage() {
		return CommandUsage.newInstance(parser);
	}

	/**
	 * Gets the usage with error.
	 *
	 * @param err the err
	 * @return the usage with error
	 */
	private Command getUsageWithError(String err) {
		return CommandUsage.newInstance(parser, err);
	}

}
