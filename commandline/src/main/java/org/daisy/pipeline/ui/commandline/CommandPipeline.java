package org.daisy.pipeline.ui.commandline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Properties;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

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
	//private Logger mLogger;
	public CommandPipeline(Properties args) {
		super(args);
		//mLogger = LoggerFactory.getLogger(this.getClass());
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
		HashMap<String, String> inputs = CommandHelper.parseInputList(mArgs
				.getProperty(INPUT));
		HashMap<String, String> outputs = CommandHelper.parseInputList(mArgs
				.getProperty(OUTPUT));
		HashMap<String, String> options = CommandHelper.parseInputList(mArgs
				.getProperty(OPTIONS));

		HashMap<String, HashMap<String, String>> params = parseParamsList(mArgs
				.getProperty(PARAMS));
		String output = null;
		
		
		ServiceProvider prov=(ServiceProvider)mArgs.get(PROVIDER); 
		XProcRunnable xpr = prov.getDaisyPipelineContext().newXprocRunnalble();
		xpr.setPipelineUri(URI.create(mArgs
				.getProperty(PIPELINE)));
	
		
		
		// bind inputs
		for (String key : inputs.keySet()) {
			InputPort port = new InputPort(key);
			port.addBind(SAXHelper.getSaxSource(inputs.get(key)));
			xpr.addInputPort(port);

		}
		// set params
		/*
		for (String port : params.keySet()) {
			for (String param : params.get(port).keySet())
				xproc.setParameter(port, param, params.get(port).get(param));

		}*/
		//options
		for (String option:options.keySet()){
			xpr.addOption(new NamedValue(option, options.get(option)));
		}
		// bind outputs

		for (String key : outputs.keySet()) {
			OutputPort port = new OutputPort(key);
			port.addBind(SAXHelper.getSaxResult(outputs.get(key)));
			xpr.addOutputPort(port);
		}
		
		

		prov.getDaisyPipelineContext().getExecutor().execute(xpr);

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
