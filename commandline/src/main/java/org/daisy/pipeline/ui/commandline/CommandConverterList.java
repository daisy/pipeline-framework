package org.daisy.pipeline.ui.commandline;


public class CommandConverterList implements Command {

	public static final String PROVIDER = "provider";

	public CommandConverterList() {
	}

	@Override
	public void execute() throws IllegalArgumentException {
//		if (!mArgs.containsKey(PROVIDER)) {
//			throw new IllegalArgumentException(
//					"Exepecting provider as an argument");
//		}
//		
//		ServiceProvider provider = null;
//		
//		try {
//			provider = (ServiceProvider) mArgs.get(PROVIDER);
//		} catch (ClassCastException cce) {
//			throw new IllegalArgumentException(
//					"Provider is not a ServiceProvider");
//		}
//		
//		Iterable<ConverterDescriptor> descriptors = provider.getDaisyPipelineContext().getConverterRegistry().getDescriptors();
//		System.out.println("Available Descriptors:");
//		for (ConverterDescriptor desc : descriptors) {
//			System.out.println(desc.getName()+" - "+desc.getDescription());
//		}

	}

}
