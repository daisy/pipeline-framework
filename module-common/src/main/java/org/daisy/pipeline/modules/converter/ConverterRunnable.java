package org.daisy.pipeline.modules.converter;

import java.util.HashMap;

import org.daisy.pipeline.modules.converter.Converter.ConverterArgument;
/**
 * The Class ConverterRunnable allows to run a converter instance setting the 
 * parameters
 */
public abstract class ConverterRunnable implements Runnable {

	/** The executor. */
	protected ConverterExecutor mExecutor;
	
	/** The arguments. */
	protected  HashMap<ConverterArgument, ValuedConverterArgument> mArguments = new HashMap<ConverterArgument, ConverterRunnable.ValuedConverterArgument>();
	
	/** The converter. */
	private Converter mConverter;
	
	/**
	 * Instantiates a new converter runnable attached to a converter instance
	 *
	 * @param conv the conv
	 */
	protected ConverterRunnable(Converter conv){
		this.setConverter(conv);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
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
	
	/**
	 * Sets the converter.
	 *
	 * @param converter the new converter
	 */
	protected void setConverter(Converter converter){
		mConverter=converter;
		mArguments=new HashMap<Converter.ConverterArgument, ConverterRunnable.ValuedConverterArgument>();
		for (ConverterArgument arg: mConverter.getArguments()){
			mArguments.put(arg,new ValuedConverterArgument(null, arg));
		}
	}

	/**
	 * Sets the value to given argument
	 *
	 * @param arg the arg
	 * @param value the value
	 */
	public void setValue(ConverterArgument arg, String value) {
		if (mArguments.containsKey(arg)) {
			mArguments.put(arg, new ValuedConverterArgument(value, arg));
		} else {
			throw new IllegalArgumentException("No argument " + arg.getName()
					+ " for converter " + getConverter().getName());
		}
	}
	
	/**
	 * Sets a valued argument. Valued arguments are stored using the converter argument as key
	 *
	 * @param value the new value
	 */
	public void setValue(ValuedConverterArgument value){
		if (mArguments.containsKey(value.getArgument())) {
			mArguments.put(value.getArgument(), value);
		} else {
			throw new IllegalArgumentException("No argument " + value.getArgument()
					+ " for converter " + getConverter().getName());
		}
	}
	
	/**
	 * Gets the valued argument bound to the given converter argument 
	 *
	 * @param arg the arg
	 * @return the valued argument
	 */
	public ValuedConverterArgument getValuedArgument(ConverterArgument arg) {
		return mArguments.get(arg);
	}
	
	/**
	 * Gets the value.
	 *
	 * @param name the name
	 * @return the value
	 */
	public ValuedConverterArgument getValue(String name) {
		ConverterArgument arg= mConverter.getArgument(name);
		if (arg==null)
			throw new IllegalArgumentException("No argument with name "+name+" for converter "+mConverter.getName());
		
		return mArguments.get(arg);
	}
	
	/**
	 * Gets the values.
	 *
	 * @return the values
	 */
	public Iterable <ValuedConverterArgument> getValues() {
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

	/**
	 * The Interface ConverterExecutor.
	 */
	public interface ConverterExecutor {
		
		/**
		 * the delegate class which will actually execute the converter invokation
		 *
		 * @param runnable the runnable
		 */
		public void execute(ConverterRunnable runnable);
	}

	/**
	 * The Class ValuedConverterArgument.
	 */
	public class ValuedConverterArgument {
		
		/** The m value. */
		protected String mValue;
		
		/** The m argument. */
		private ConverterArgument mArgument;

		/**
		 * Instantiates a new valued converter argument.
		 *
		 * @param value the value
		 * @param argument the argument
		 */
		public ValuedConverterArgument(String value, ConverterArgument argument) {
			super();
			mValue = value;
			mArgument = argument;
		}

		/**
		 * Sets the value.
		 *
		 * @param value the new value
		 */
		public void setValue(String value) {
			mValue = value;
		}

		/**
		 * Gets the value.
		 *
		 * @return the value
		 */
		public String getValue() {
			return mValue;
		}

		/**
		 * Sets the argument.
		 *
		 * @param argument the new argument
		 */
		public void setArgument(ConverterArgument argument) {
			mArgument = argument;
		}

		/**
		 * Gets the argument.
		 *
		 * @return the argument
		 */
		public ConverterArgument getArgument() {
			return mArgument;
		}

	}

}
