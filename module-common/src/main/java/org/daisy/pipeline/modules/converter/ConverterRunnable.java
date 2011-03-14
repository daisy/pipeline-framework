package org.daisy.pipeline.modules.converter;

import java.util.HashMap;

import org.daisy.pipeline.modules.converter.Converter.ConverterArgument;

public abstract class ConverterRunnable implements Runnable {

	protected ConverterExecutor mExecutor;
	protected  HashMap<ConverterArgument, ValuedConverterArgument> mArguments = new HashMap<ConverterArgument, ConverterRunnable.ValuedConverterArgument>();
	private Converter mConverter;
	
	protected ConverterRunnable(Converter conv){
		this.setConverter(conv);
	}
	
	public void run() {
		for (ConverterArgument arg : mArguments.keySet()) {
			if (!arg.isOptional()
					&& (mArguments.get(arg) == null
							|| mArguments.get(arg).getValue() == null || mArguments
							.get(arg).getValue().isEmpty())) {
				throw new RuntimeException("No value for mandatory argument:"+arg.getName());

			}
		}

		mExecutor.execute(this);
	}
	
	protected void setConverter(Converter converter){
		mConverter=converter;
		mArguments=new HashMap<Converter.ConverterArgument, ConverterRunnable.ValuedConverterArgument>();
		for (ConverterArgument arg: mConverter.getArguments()){
			mArguments.put(arg,new ValuedConverterArgument(null, arg));
		}
	}

	public void setValue(ConverterArgument arg, String value) {
		if (mArguments.containsKey(arg)) {
			mArguments.put(arg, new ValuedConverterArgument(value, arg));
		} else {
			throw new IllegalArgumentException("No argument " + arg.getName()
					+ " for converter " + getConverter().getName());
		}
	}
	public void setValue(ValuedConverterArgument value){
		if (mArguments.containsKey(value.getArgument())) {
			mArguments.put(value.getArgument(), value);
		} else {
			throw new IllegalArgumentException("No argument " + value.getArgument()
					+ " for converter " + getConverter().getName());
		}
	}
	public ValuedConverterArgument getValuedArgument(ConverterArgument arg) {
		return mArguments.get(arg);
	}
	public ValuedConverterArgument getValue(String name) {
		ConverterArgument arg= mConverter.getArgument(name);
		if (arg==null)
			throw new IllegalArgumentException("No argument with name "+name+" for converter "+mConverter.getName());
		
		return mArguments.get(arg);
	}
	
	public Iterable <ValuedConverterArgument> getValues() {
		return mArguments.values();
	}
	public Converter getConverter() {
		return mConverter;
	}

	public interface ConverterExecutor {
		public void execute(ConverterRunnable runnable);
	}

	public class ValuedConverterArgument {
		protected String mValue;
		private ConverterArgument mArgument;

		public ValuedConverterArgument(String value, ConverterArgument argument) {
			super();
			mValue = value;
			mArgument = argument;
		}

		public void setValue(String value) {
			mValue = value;
		}

		public String getValue() {
			return mValue;
		}

		public void setArgument(ConverterArgument argument) {
			mArgument = argument;
		}

		public ConverterArgument getArgument() {
			return mArgument;
		}

	}

}
