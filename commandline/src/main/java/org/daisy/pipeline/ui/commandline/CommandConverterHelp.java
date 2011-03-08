package org.daisy.pipeline.ui.commandline;

import java.util.Properties;

import org.daisy.pipeline.modules.converter.Converter;
import org.daisy.pipeline.modules.converter.ConverterDescriptor;
import org.daisy.pipeline.modules.converter.Converter.ConverterArgument;
import org.daisy.pipeline.ui.commandline.provider.ServiceProvider;

public class CommandConverterHelp extends Command {

	public static final String NAME = "name";
	public static final String PROVIDER = "provider";

	public CommandConverterHelp(Properties args) {
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
		ServiceProvider provider = null;
		
		try {
			provider = (ServiceProvider) mArgs.get(PROVIDER);
		} catch (ClassCastException cce) {
			throw new IllegalArgumentException(
					"Provider is not a ServiceProvider");
		}
		ConverterDescriptor desc = provider.getConverterRegistry().getDescriptor(mArgs.getProperty(NAME));
		if (desc==null){
			throw new RuntimeException(
			"Converter:"+mArgs.getProperty(NAME)+" not found");
		}
		Converter conv = desc.getConverter();
		Iterable<ConverterArgument> args= conv.getArguments();
		System.out.println(conv.getName()+":");
		System.out.println(conv.getDescription());
		System.out.println("Arguments:");
		for (ConverterArgument arg : args) {
			String s = arg.getName()+":\t  "+arg.getDesc();
			if(arg.isOptional()){
				s+=" (optional) ";
			}
			System.out.println(s);
		}

	}

}
