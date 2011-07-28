package org.daisy.pipeline.modules.converter;

import java.net.URI;



/**
 * Inmutable class for accessing converter elements.
 */
public interface Converter {
	public URI getURI();
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
		
		public void setURI(URI uri);
	}

	
}
