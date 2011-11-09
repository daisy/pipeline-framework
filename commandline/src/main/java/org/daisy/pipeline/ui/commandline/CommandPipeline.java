package org.daisy.pipeline.ui.commandline;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.daisy.common.base.Provider;
import org.daisy.common.xproc.XProcEngine;
import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcOutput;
import org.daisy.common.xproc.XProcPipeline;
import org.daisy.common.xproc.XProcResult;

// TODO: Auto-generated Javadoc
/**
 * Class CommandPipeline executes pipelines using the daisy pipeline 2 framework
 */
public class CommandPipeline implements Command {

	/**
	 * New instance using the the parameters provided using the CLI 
	 *
	 * @param pipeline the pipeline
	 * @param inputArgs the input args
	 * @param outputArgs the output args
	 * @param paramsArgs the params args
	 * @param optionsArgs the options args
	 * @param xprocEngine the xproc engine
	 * @return the command
	 */
	public static Command newInstance(String pipeline, String inputArgs,
			String outputArgs, String paramsArgs, String optionsArgs,
			XProcEngine xprocEngine) {
		return new CommandPipeline(pipeline, inputArgs, outputArgs, paramsArgs,
				optionsArgs, xprocEngine);
	}

	/** The xproc engine. */
	private final XProcEngine xprocEngine;
	
	/** The uri. */
	private final String uri;
	
	/** The inputs. */
	private final Map<String, String> inputs;
	
	/** The outputs. */
	private final Map<String, String> outputs;
	
	/** The options. */
	private final Map<String, String> options;// FIXME use QNames
	
	/** The params. */
	private final Map<String, HashMap<String, String>> params;

	/**
	 * Instantiates a new command pipeline.
	 *
	 * @param uri the uri
	 * @param inputArgs the input args
	 * @param outputArgs the output args
	 * @param paramsArgs the params args
	 * @param optionsArgs the options args
	 * @param xprocEngine the xproc engine
	 */
	private CommandPipeline(String uri, String inputArgs, String outputArgs,
			String paramsArgs, String optionsArgs, XProcEngine xprocEngine) {
		if (uri == null || uri.isEmpty()) {
			throw new IllegalArgumentException("Error: no XProc document URI");
		}
		this.uri = uri;
		this.inputs = CommandHelper.parseInputList(inputArgs);
		this.outputs = CommandHelper.parseInputList(outputArgs);
		this.params = parseParamsList(paramsArgs);
		this.options = CommandHelper.parseInputList(optionsArgs);
		this.xprocEngine = xprocEngine;
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.ui.commandline.Command#execute()
	 */
	@Override
	public void execute() throws IllegalArgumentException {

		XProcPipeline pipeline = xprocEngine.load(URI.create(uri));

		// bind inputs
		XProcInput.Builder inputBuilder = new XProcInput.Builder();
		for (final String port : inputs.keySet()) {
			inputBuilder.withInput(port, new Provider<Source>() {

				@Override
				public Source provide() {
					return SAXHelper.getSaxSource(inputs.get(port));
				}
			});
		}

		// bind params
		for (final String port : params.keySet()) {
			for (String param : params.get(port).keySet()) {
				inputBuilder.withParameter(port, new QName(param),
						params.get(port).get(param));
			}
		}

		// bind options
		for (String option : options.keySet()) {
			inputBuilder.withOption(new QName(option), options.get(option));
		}

		// bind outputs
		XProcOutput.Builder outputBuilder = new XProcOutput.Builder();
		for (final String port : outputs.keySet()) {
			outputBuilder.withOutput(port, new Provider<Result>() {

				@Override
				public Result provide() {
					return SAXHelper.getSaxResult(outputs.get(port));
				}
			});
		}

		XProcResult result = pipeline.run(inputBuilder.build());
		result.writeTo(outputBuilder.build());
	}

	/**
	 * Parses the params list.
	 *
	 * @param list the list
	 * @return the hash map
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public HashMap<String, HashMap<String, String>> parseParamsList(String list)
			throws IllegalArgumentException {

		HashMap<String, HashMap<String, String>> pairs = new HashMap<String, HashMap<String, String>>();
		if (list.isEmpty())
			return pairs;

		String[] parts = list.split(",");
		for (String part : parts) {
			String pair[] = part.split("=");
			try {
				if (!pairs.containsKey(pair[0])) {
					pairs.put(pair[0], new HashMap<String, String>());
				}
				pairs.get(pair[0]).put(pair[1], pair[2]);
			} catch (Exception e) {
				throw new IllegalArgumentException("Error in list format:"
						+ list);
			}
		}
		if (pairs.containsKey(null) || pairs.containsKey("")) {
			throw new IllegalArgumentException("Error in list format:" + list);
		}
		if (pairs.containsValue(null) || pairs.containsValue("")) {
			throw new IllegalArgumentException("Error in list format:" + list);
		}
		return pairs;
	}

}
