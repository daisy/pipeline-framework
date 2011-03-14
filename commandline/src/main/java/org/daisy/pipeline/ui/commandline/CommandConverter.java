package org.daisy.pipeline.ui.commandline;

import java.util.HashMap;
import java.util.Properties;

import org.daisy.pipeline.modules.converter.Converter;
import org.daisy.pipeline.modules.converter.ConverterDescriptor;
import org.daisy.pipeline.modules.converter.ConverterRunnable;
import org.daisy.pipeline.modules.converter.Converter.ConverterArgument;
import org.daisy.pipeline.modules.converter.ConverterRunnable.ValuedConverterArgument;
import org.daisy.pipeline.ui.commandline.provider.ServiceProvider;

public class CommandConverter extends Command {

	public static final String NAME = "name";
	public static final String PROVIDER = "provider";
	public static final String ARGS = "args";

	public CommandConverter(Properties args) {
		super(args);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() throws IllegalArgumentException {
		if (!mArgs.containsKey(PROVIDER)) {
			throw new IllegalArgumentException(
					"Exepecting provider as an argument");
		}
		if (!mArgs.containsKey(NAME)) {
			throw new IllegalArgumentException(
					"Exepecting converter name");
		}
		
		if (!mArgs.containsKey(ARGS)) {
			throw new IllegalArgumentException(
					"Exepecting args ");
		}
		
		ServiceProvider provider = null;
		
		try {
			provider = (ServiceProvider) mArgs.get(PROVIDER);
		} catch (ClassCastException cce) {
			throw new IllegalArgumentException(
					"Provider is not a ServiceProvider");
		}
		
		HashMap<String, String> strArgs = CommandHelper.parseInputList(mArgs.getProperty(ARGS));
		ConverterDescriptor desc = provider.getConverterRegistry().getDescriptor(mArgs.getProperty(NAME));
		if (desc==null){
			throw new RuntimeException(
			"Converter:"+mArgs.getProperty(NAME)+" not found");
		}
		ConverterRunnable runnable = desc.getConverter().getRunnable();
		for(String argName : strArgs.keySet()){
			runnable.getValue(argName).setValue(strArgs.get(argName));
		}
		runnable.run();
	}

}
