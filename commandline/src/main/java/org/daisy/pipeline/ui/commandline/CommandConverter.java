package org.daisy.pipeline.ui.commandline;


public class CommandConverter implements Command {

	public static final String NAME = "name";
	public static final String PROVIDER = "provider";
	public static final String ARGS = "args";

	public CommandConverter() {
	}

	@Override
	public void execute() throws IllegalArgumentException {
//		if (!mArgs.containsKey(PROVIDER)) {
//			throw new IllegalArgumentException(
//					"Exepecting provider as an argument");
//		}
//		if (!mArgs.containsKey(NAME)) {
//			throw new IllegalArgumentException(
//					"Exepecting converter name");
//		}
//		
//		if (!mArgs.containsKey(ARGS)) {
//			throw new IllegalArgumentException(
//					"Exepecting args ");
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
//		HashMap<String, String> strArgs = CommandHelper.parseInputList(mArgs.getProperty(ARGS));
//		ConverterDescriptor desc = provider.getConverterRegistry().getDescriptor(mArgs.getProperty(NAME));
//		if (desc==null){
//			throw new RuntimeException(
//			"Converter:"+mArgs.getProperty(NAME)+" not found");
//		}
//		ConverterRunnable runnable = desc.getConverter().getRunnable();
//		for(String argName : strArgs.keySet()){
//			ConverterArgument arg=runnable.getConverter().getArgument(argName);
//			ValuedConverterArgument  varg= null;
//			if(arg.getBindType()==BindType.PORT && arg.getDirection()==Direction.INPUT)
//				varg=arg.getValuedConverterBuilder().withSource(SAXHelper.getSaxSource(strArgs.get(argName)));
//			else if(arg.getBindType()==BindType.PORT && arg.getDirection()==Direction.OUTPUT)
//				varg=arg.getValuedConverterBuilder().withResult(SAXHelper.getSaxResult(strArgs.get(argName)));
//			else if(arg.getBindType()==BindType.OPTION){
//				varg=arg.getValuedConverterBuilder().withString(strArgs.get(argName));
//			}
//			runnable.setConverterArgumentValue(varg);
//		}
// 
//		
//		//provider.getDaisyPipelineContext().getJobManager().addJob(runnable);
//		//making things more complex :
//		runnable.setExecutor(provider.getDaisyPipelineContext().getExecutor());
//		runnable.run();
	}

}
