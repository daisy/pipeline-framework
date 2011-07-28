package org.daisy.pipeline.ui.commandline;

import java.util.HashMap;
import java.util.Properties;

import org.daisy.pipeline.modules.converter.ConverterArgument;
import org.daisy.pipeline.modules.converter.ConverterArgument.Direction;
import org.daisy.pipeline.modules.converter.ConverterArgument.ValuedArgumentBuilder;
import org.daisy.pipeline.modules.converter.ConverterArgument.ValuedConverterArgument;
import org.daisy.pipeline.modules.converter.ConverterDescriptor;
import org.daisy.pipeline.modules.converter.ConverterRunnable;
import org.daisy.pipeline.modules.converter.ConverterArgument.BindType;
import org.daisy.pipeline.ui.commandline.provider.ServiceProvider;

import com.sun.org.apache.bcel.internal.generic.GETSTATIC;

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
			ConverterArgument arg=runnable.getConverter().getArgument(argName);
			ValuedConverterArgument  varg= null;
			if(arg.getBindType()==BindType.PORT && arg.getDirection()==Direction.INPUT)
				varg=arg.getValuedConverterBuilder().withSource(SAXHelper.getSaxSource(strArgs.get(argName)));
			else if(arg.getBindType()==BindType.PORT && arg.getDirection()==Direction.OUTPUT)
				varg=arg.getValuedConverterBuilder().withResult(SAXHelper.getSaxResult(strArgs.get(argName)));
			else if(arg.getBindType()==BindType.OPTION){
				varg=arg.getValuedConverterBuilder().withString(strArgs.get(argName));
			}
			runnable.setConverterArgumentValue(varg);
		}
 
		
		//provider.getDaisyPipelineContext().getJobManager().addJob(runnable);
		//making things more complex :
		runnable.setExecutor(provider.getDaisyPipelineContext().getExecutor());
		runnable.run();
	}

}
