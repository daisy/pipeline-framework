package org.daisy.pipeline.ui.commandline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.daisy.common.base.Provider;
import org.daisy.common.xproc.XProcEngine;
import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcPipeline;
import org.daisy.pipeline.modules.converter.XProcRunnable;
import org.daisy.pipeline.ui.commandline.provider.ServiceProvider;
import org.daisy.pipeline.xproc.InputPort;
import org.daisy.pipeline.xproc.NamedValue;
import org.daisy.pipeline.xproc.OutputPort;
import org.xml.sax.InputSource;

public class CommandPipeline extends Command {
	public static String INPUT = "INPUT";
	public static String PIPELINE = "PIPELINE";
	public static String OUTPUT = "OUTPUT";
	public static String PARAMS = "PARAMS";
	public static String OPTIONS = "OPTIONS";
	public static String PROVIDER = "PROVIDER";

	// private Logger mLogger;
	public CommandPipeline(Properties args) {
		super(args);
		// mLogger = LoggerFactory.getLogger(this.getClass());
	}

	@Override
	public void execute() throws IllegalArgumentException {
		if (!mArgs.containsKey(PROVIDER)) {
			throw new IllegalArgumentException(
					"Exepecting provider as an argument");
		}

		if (mArgs.getProperty(PIPELINE).isEmpty()) {
			throw new IllegalArgumentException("Error:No pipeline file");
		}
		final HashMap<String, String> inputs = CommandHelper
				.parseInputList(mArgs.getProperty(INPUT));
		HashMap<String, String> outputs = CommandHelper.parseInputList(mArgs
				.getProperty(OUTPUT));
		HashMap<String, String> options = CommandHelper.parseInputList(mArgs
				.getProperty(OPTIONS));

		HashMap<String, HashMap<String, String>> params = parseParamsList(mArgs
				.getProperty(PARAMS));
		String output = null;

		ServiceProvider prov = (ServiceProvider) mArgs.get(PROVIDER);
		XProcEngine xprocEngine = null;//FIXME
		XProcPipeline pipeline = xprocEngine.load(URI.create(mArgs
				.getProperty(PIPELINE)));
		// XProcRunnable xpr =
		// prov.getDaisyPipelineContext().newXprocRunnalble();
		// xpr.setPipelineUri(URI.create(mArgs
		// .getProperty(PIPELINE)));

		XProcInput.Builder inputBuilder = new XProcInput.Builder();
		// bind inputs
		for (final String key : inputs.keySet()) {
			inputBuilder.withInput(key, new Provider<Source>() {

				@Override
				public Source provide() {
					return SAXHelper.getSaxSource(inputs.get(key));
				}
			});
			// InputPort port = new InputPort(key);
			// port.addBind(SAXHelper.getSaxSource(inputs.get(key)));
			// xpr.addInputPort(port);

		}
		// set params
		/*
		 * for (String port : params.keySet()) { for (String param :
		 * params.get(port).keySet()) xproc.setParameter(port, param,
		 * params.get(port).get(param));
		 * 
		 * }
		 */
		// options
		for (String option : options.keySet()) {
			inputBuilder.withOption(new QName(option), options.get(option));
			// xpr.addOption(new NamedValue(option, options.get(option)));
		}
		// bind outputs

		for (String key : outputs.keySet()) {
			OutputPort port = new OutputPort(key);
			port.addBind(SAXHelper.getSaxResult(outputs.get(key)));
			// xpr.addOutputPort(port);
		}

		pipeline.run(inputBuilder.build());
		// prov.getDaisyPipelineContext().getExecutor().execute(xpr);

	}

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
