package org.daisy.pipeline.ui.commandline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Properties;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import net.sf.saxon.Configuration;

import org.daisy.pipeline.modules.UriResolverDecorator;
import org.daisy.pipeline.ui.commandline.provider.ServiceProvider;
import org.daisy.pipeline.xproc.XProcessor;
import org.daisy.pipeline.xproc.XProcessorFactory;
import org.xml.sax.InputSource;

public class CommandPipeline extends Command {
	public static String INPUT = "INPUT";
	public static String PIPELINE = "PIPELINE";
	public static String OUTPUT = "OUTPUT";
	public static String PARAMS = "PARAMS";
	public static String OPTIONS = "OPTIONS";
	public static String PROVIDER = "PROVIDER";

	public CommandPipeline(Properties args) {
		super(args);

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
		HashMap<String, String> inputs = parseInputList(mArgs
				.getProperty(INPUT));
		HashMap<String, String> outputs = parseInputList(mArgs
				.getProperty(OUTPUT));
		HashMap<String, String> options = parseInputList(mArgs
				.getProperty(OPTIONS));

		HashMap<String, HashMap<String, String>> params = parseParamsList(mArgs
				.getProperty(PARAMS));
		String output = null;

		XProcessorFactory fact = ((ServiceProvider) mArgs.get(PROVIDER))
				.getXProcessorFactory();

		XProcessor xproc = fact.getProcessor(getSaxSource(mArgs
				.getProperty(PIPELINE)));
		// Uri resolver settings
		URIResolver defaultResolver = Configuration.newConfiguration().getURIResolver();
		UriResolverDecorator uriResolver = ((ServiceProvider) mArgs
				.get(PROVIDER)).getUriResolver().setDelegatedUriResolver(
				defaultResolver);
		xproc.setURIResolver(uriResolver);
		// bind inputs
		for (String key : inputs.keySet()) {

			xproc.bindInputPort(key, getSaxSource(inputs.get(key)));

		}
		// set params
		for (String port : params.keySet()) {
			for (String param : params.get(port).keySet())
				xproc.setParameter(port, param, params.get(port).get(param));

		}
		//options
		for (String option:options.keySet()){
			xproc.setOption(option, options.get(option));
		}
		// bind outputs

		for (String key : outputs.keySet()) {

			xproc.bindOutputPort(key, getSaxResult(outputs.get(key)));

		}
		
		
		// here we go!

		xproc.run();

	}

	private Source getSaxSource(String path) throws IllegalArgumentException {
		File file = new File(path);
		if (!file.exists() || !file.canRead()) {
			throw new IllegalArgumentException(
					"Error: file not found or its not readable:" + path);
		}
		return new SAXSource(new InputSource(file.toURI().toString()));
	}

	private Result getSaxResult(String output) throws IllegalArgumentException {
		if (output == null || output.isEmpty()) {
			return new StreamResult(System.out);
		} else {

			try {
				return new StreamResult(new FileOutputStream(output));
			} catch (FileNotFoundException e) {
				throw new IllegalArgumentException("Output file not found:" + e);
			}

		}

	}

	public HashMap<String, String> parseInputList(String list)
			throws IllegalArgumentException {

		HashMap<String, String> pairs = new HashMap<String, String>();
		if (list.isEmpty())
			return pairs;

		String[] parts = list.split(",");
		for (String part : parts) {
			String pair[] = part.split(":");
			try {
				pairs.put(pair[0], pair[1]);
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

	public HashMap<String, HashMap<String, String>> parseParamsList(String list)
			throws IllegalArgumentException {

		HashMap<String, HashMap<String, String>> pairs = new HashMap<String, HashMap<String, String>>();
		if (list.isEmpty())
			return pairs;

		String[] parts = list.split(",");
		for (String part : parts) {
			String pair[] = part.split(":");
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
