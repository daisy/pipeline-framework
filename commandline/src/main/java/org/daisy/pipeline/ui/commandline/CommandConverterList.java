package org.daisy.pipeline.ui.commandline;

import java.net.URI;
import java.util.Properties;

import org.daisy.pipeline.modules.converter.ConverterDescriptor;
import org.daisy.pipeline.ui.commandline.provider.ServiceProvider;

public class CommandConverterList extends Command {

	public static final String PROVIDER = "provider";

	public CommandConverterList(Properties args) {
		super(args);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() throws IllegalArgumentException {
		if (!mArgs.containsKey(PROVIDER)) {
			throw new IllegalArgumentException(
					"Exepecting provider as an argument");
		}
		
		ServiceProvider provider = null;
		
		try {
			provider = (ServiceProvider) mArgs.get(PROVIDER);
		} catch (ClassCastException cce) {
			throw new IllegalArgumentException(
					"Provider is not a ServiceProvider");
		}
		
		Iterable<ConverterDescriptor> descriptors = provider.getDaisyPipelineContext().getConverterRegistry().getDescriptors();
		System.out.println("Available Descriptors:");
		for (ConverterDescriptor desc : descriptors) {
			System.out.println(desc.getName()+" - "+desc.getDescription());
		}

	}

}
