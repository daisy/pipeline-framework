package org.daisy.pipeline.modules.converter;

import org.daisy.pipeline.modules.converter.Converter.ConverterArgument.Type;


/**
 * Inmutable class for accessing converter elements.
 */
public interface Converter {
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName();

	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public String getVersion();

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription();

	/**
	 * Gets the converter argument associated with the name 
	 *
	 * @param name the name of the argument to retrieve
	 * @return the argument
	 */
	public ConverterArgument getArgument(String name);

	/**
	 * Gets the arguments as a iterable
	 *
	 * @return the arguments
	 */
	public Iterable<ConverterArgument> getArguments();

	/**
	 * Gets the factory to build new converters
	 *
	 * @return the factory
	 */
	public ConverterFactory getFactory();

	/**
	 * Returns a converter of this kind that can be run. 
	 *
	 * @return the runnable
	 */
	public ConverterRunnable getRunnable();

	/**
	 * The Class ConverterArgument defines an inmutable converter argument
	 */
	public abstract class ConverterArgument {
		
		/**
		 * The Enum with the different types of converter arguments.
		 */
		public enum Type {
			
			/** INPUT arg */
			INPUT, 
			/** OUTPUT.arg */
			OUTPUT, 
			/** OPTION arg */
			OPTION, 
			/** PARAMETER arg */
			PARAMETER;

		};

		/** The  argument name */
		protected String mName;
		
		/** The argument type. */
		protected Type mType;
		
		/** The  associated port . */
		protected String mPort;
		
		/** The  string to bind this arg . */
		protected String mBind;
		
		/** argument description */
		protected String mDesc;
		
		/** indicates if this argument is optional */
		protected boolean mOptional;

		/**
		 * Instantiates a new converter argument.
		 */
		public ConverterArgument(){
			
		}
		
		/**
		 * Instantiates a new converter argument.
		 *
		 * @param name the name
		 * @param type the type
		 * @param port the port
		 * @param bind the bind
		 * @param desc the desc
		 * @param optional the optional
		 */
		public ConverterArgument(String name, Type type, String port,
				String bind, String desc, boolean optional) {
			super();
			mName = name;
			mType = type;
			mPort = port;
			mBind = bind;
			mDesc = desc;
			mOptional = optional;
		}

		/**
		 * Gets the name.
		 *
		 * @return the name
		 */
		public String getName() {
			return mName;
		}

		/**
		 * Gets the type.
		 *
		 * @return the type
		 */
		public Type getType() {
			return mType;
		}

		/**
		 * Gets the port.
		 *
		 * @return the port
		 */
		public String getPort() {
			return mPort;
		}

		/**
		 * Sets the port.
		 *
		 * @param port the new port
		 */
		public void setPort(String port) {
			mPort = port;
		}

		/**
		 * Gets the bind.
		 *
		 * @return the bind
		 */
		public String getBind() {
			return mBind;
		}

		/**
		 * Gets the desc.
		 *
		 * @return the desc
		 */
		public String getDesc() {
			return mDesc;
		}

		/**
		 * Checks if is optional.
		 *
		 * @return true, if is optional
		 */
		public boolean isOptional() {
			return mOptional;
		}

	}

	/**
	 * Adds setters and getters to the Inmutable interface
	 */
	public interface MutableConverter extends Converter{
		
		/**
		 * Sets the name.
		 *
		 * @param name the new name
		 */
		public void setName(String name);

		/**
		 * Sets the version.
		 *
		 * @param version the new version
		 */
		public void setVersion(String version);

		/**
		 * Sets the description.
		 *
		 * @param description the new description
		 */
		public void setDescription(String description);

		/**
		 * Adds the argument.
		 *
		 * @param argument the argument
		 */
		public void addArgument(ConverterArgument argument);
	}

	/**
	 * mutable interface for a converter argument.
	 */
	public abstract class  MutableConverterArgument extends ConverterArgument{
		
		/**
		 * Sets the name.
		 *
		 * @param name the new name
		 */
		public abstract void setName(String name);

		/* (non-Javadoc)
		 * @see org.daisy.pipeline.modules.converter.Converter.ConverterArgument#setPort(java.lang.String)
		 */
		public abstract void setPort(String port);

		/**
		 * Sets the bind.
		 *
		 * @param bind the new bind
		 */
		public abstract void setBind(String bind);

		/**
		 * Sets the desc.
		 *
		 * @param desc the new desc
		 */
		public abstract void setDesc(String desc);

		/**
		 * Sets the type.
		 *
		 * @param type the new type
		 */
		public abstract void setType(Type type);

		/**
		 * Sets the optional.
		 *
		 * @param optional the new optional
		 */
		public abstract void setOptional(boolean optional);
	}
}
