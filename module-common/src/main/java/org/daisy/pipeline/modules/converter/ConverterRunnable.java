package org.daisy.pipeline.modules.converter;

import java.util.HashMap;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.daisy.pipeline.modules.converter.ConverterArgument.BindType;
import org.daisy.pipeline.modules.converter.ConverterArgument.Direction;
import org.daisy.pipeline.modules.converter.ConverterArgument.ValuedConverterArgument;
import org.daisy.pipeline.xproc.InputPort;
import org.daisy.pipeline.xproc.NamedValue;
import org.daisy.pipeline.xproc.OutputPort;
import org.daisy.pipeline.xproc.Port;
/**
 * The Class ConverterRunnable allows to run a converter instance setting the 
 * parameters
 */
public class ConverterRunnable extends XProcRunnable {

	
	/** The arguments. */
	protected  HashMap<ConverterArgument, ValuedConverterArgument> mArguments = new HashMap<ConverterArgument, ValuedConverterArgument>();
	protected HashMap<String, Port> mPorts= new HashMap<String, Port>();
	/** The converter. */
	private Converter mConverter;
	
	/**
	 * Instantiates a new converter runnable attached to a converter instance
	 *
	 * @param conv the conv
	 */
	public ConverterRunnable(Converter conv){
		this.setConverter(conv);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run(){
		for (ConverterArgument arg : mArguments.keySet()) {
			if (!arg.isOptional()
					&& (mArguments.get(arg) == null
							|| mArguments.get(arg).getValues().isEmpty()  )) {
				throw new RuntimeException("No value for mandatory argument:"+arg.getName());

			}
		}
		this.bind();
		super.run();
	}
	
	
	@SuppressWarnings("unchecked")
	protected void bind() {
	
			for (ValuedConverterArgument arg : mArguments.values()) {
				if (arg.getConverterArgument().getBindType() == BindType.PORT && arg.getConverterArgument().getDirection() == Direction.INPUT) {
					bindInput((ValuedConverterArgument<Source>)arg);
				}else if (arg.getConverterArgument().getBindType() == BindType.PORT && arg.getConverterArgument().getDirection() == Direction.OUTPUT){
					bindOutput((ValuedConverterArgument<Result>)arg);
				}else if (arg.getConverterArgument().getBindType() == BindType.OPTION){
					bindOption((ValuedConverterArgument<String>)arg);
				}
			}
	

	}
	
	
	private void bindOption(ValuedConverterArgument<String> arg) {
		if(arg.getValues().isEmpty()){
			throw new IllegalArgumentException("no value passed for the option:"+arg.getConverterArgument().getBind());
		}
		NamedValue opt = new NamedValue(arg.getConverterArgument().getBind(), arg.getValues().get(0));
		super.mOptions.add(opt);
		
	}

	private void bindInput(ValuedConverterArgument<Source> arg) {
		InputPort port = (InputPort) mPorts.get(arg.getConverterArgument().getBind());
		if(port==null){
			port= new InputPort(arg.getConverterArgument().getBind());
			this.mPorts.put(arg.getConverterArgument().getBind(), port);
			this.addInputPort(port);
		} 
		for (Source src: arg.getValues()){
			port.addBind(src);
		}
	}

	private void bindOutput(ValuedConverterArgument<Result> arg) {
		OutputPort port = (OutputPort) mPorts.get(arg.getConverterArgument().getBind());
		if(port==null){
			port= new OutputPort(arg.getConverterArgument().getBind());
			this.mPorts.put(arg.getConverterArgument().getBind(), port);
			this.addOutputPort(port);
		} 
		for (Result res: arg.getValues()){
			port.addBind(res);
		}
	}
	
	/**
	 * Sets the converter.
	 *
	 * @param converter the new converter
	 */
	protected void setConverter(Converter converter){
		mConverter=converter;
		mArguments=new HashMap<ConverterArgument, ValuedConverterArgument>();
		for (ConverterArgument arg: mConverter.getArguments()){
			mArguments.put(arg,null);
		}
		this.setPipelineUri(this.getConverter().getURI());
	}

	
	public void setConverterArgumentValue(ValuedConverterArgument value){ 
		mArguments.put(value.getConverterArgument(), value);
	}
	

	

	
	/**
	 * Gets the values.
	 *
	 * @return the values
	 */
	protected Iterable <ValuedConverterArgument> getValues() {
		return mArguments.values();
	}
	
	/**
	 * Gets the converter.
	 *
	 * @return the converter
	 */
	public Converter getConverter() {
		return mConverter;
	}



	
}
