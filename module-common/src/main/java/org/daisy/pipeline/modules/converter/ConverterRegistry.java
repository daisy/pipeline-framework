package org.daisy.pipeline.modules.converter;

import java.net.URI;


// TODO: Auto-generated Javadoc
/**
 * The Interface ConverterRegistry to keep track of the available descriptors. This class only operates 
 * with converter descriptors
 */
public interface ConverterRegistry {

	/**
	 * Adds a new  converter descriptor.
	 *
	 * @param conv the conv
	 */
	public void addConverterDescriptor(ConverterDescriptor conv);
	
	/**
	 * Gets all the available descriptors 
	 *
	 * @return the descriptors
	 */
	public Iterable<ConverterDescriptor> getDescriptors();
	
	/**
	 * Gets the descriptor bound to the name
	 *
	 * @param name the name
	 * @return the descriptor
	 */
	public ConverterDescriptor getDescriptor(String name);
	/**
	 * Gets the descriptor bound to the uri
	 *
	 * @param name the name
	 * @return the descriptor
	 */
	public ConverterDescriptor getDescriptor(URI uri);
	
}
