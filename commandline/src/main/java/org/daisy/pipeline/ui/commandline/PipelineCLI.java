package org.daisy.pipeline.ui.commandline;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.daisy.common.xproc.XProcEngine;
import org.daisy.pipeline.modules.ModuleRegistry;
import org.daisy.pipeline.script.ScriptRegistry;
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
		parser.accepts("l", "List of available uris");
		parser.accepts(
				"c",
				"List of available converters or if a converter name is present it will be executed using the -a arguments")
				.withOptionalArg().ofType(String.class)
				.describedAs("converter");
		parser.accepts("x", "xproc file to execute").withRequiredArg();
		parser.accepts(
				"i",
				"list of input ports in the format portName1=file1,portName2=file2  (only with -x modifier)")
				.withRequiredArg();
		parser.accepts("o",
				"list of output ports in the format portName1=file1,portName2=file2")
				.withRequiredArg();
		parser.accepts(
				"p",
				"list of parameters in the format port1=param1=value1,port1=param2=value2 (only with -x modifier)")
				.withRequiredArg();
		parser.accepts("t",
				"list of options in the format opt1=value1,opt2=value2 (only with -x modifier)")
				.withRequiredArg();
		parser.accepts(
				"a",
				"list of arguments in the format arg1=value1,arg2=value2 (only with -c modifier)")
				.withRequiredArg();
		parser.accepts("h",
				"Showe this help or the help for the given converter")
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
							StringWriter sw = new StringWriter();
							e.printStackTrace(new PrintWriter(sw));

							getUnrecovreableError(
									e.getMessage() + "\n" + sw.toString())
									.execute();
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
			return getListCommand();
		} else if (oSet.has("c")) {
			return getConverterCommand(oSet);
		} else if (oSet.has("x")) {
			return getPipelineCommand(oSet);
		} else if (oSet.has("h")) {
			return getHelpCommand(oSet);
		}

		// at this point the only option left should be help
		return getUsage();
	}

	private Command getHelpCommand(OptionSet oSet) {
		// FIXME
		return getUsage();
		// Properties commandArgs = new Properties();
		// commandArgs.put(CommandConverterHelp.PROVIDER, mProvider);
		//
		// if (oSet.valueOf("h") != null
		// && !oSet.valueOf("h").toString().isEmpty()) {
		// commandArgs.put(CommandConverterHelp.NAME, oSet.valueOf("h")
		// .toString());
		// return new CommandConverterHelp(commandArgs);
		// } else
		// return getUsage();
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
		return CommandPipeline.newInstance(pipeline, inputs, outputs, params, options, xprocEngine);
	}

	private Command getConverterCommand(OptionSet oSet) {
		// FIXME
		return getUsage();
		// Properties commandArgs = new Properties();
		// commandArgs.put(CommandList.PROVIDER, mProvider);
		// // System.out.println(oSet.valueOf("c"));
		// if (oSet.valueOf("c") == null) {
		// // return new CommandConverterList(commandArgs);
		// } else {
		// String arguments;
		// String name = oSet.valueOf("c").toString();
		// if (oSet.valueOf("a") != null) {
		// arguments = oSet.valueOf("a").toString();
		// } else {
		// return this
		// .getUnrecovreableError("converter without arguments");
		// }
		// commandArgs.put(CommandConverter.PROVIDER, mProvider);
		// commandArgs.setProperty(CommandConverter.NAME, name);
		// commandArgs.setProperty(CommandConverter.ARGS, arguments);
		// return new CommandConverter(commandArgs);
		// }
	}

	private Command getListCommand() {
		return CommandList.newInstance(moduleRegistry);
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
