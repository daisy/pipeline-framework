package org.daisy.pipeline.job;

import java.io.InputStream;

import org.daisy.common.base.Provider;


/**
 * The Interface ResourceCollection defines a set of resources and methods to access them
 */
public interface ResourceCollection {
	
	/**
	 * Gets the resources.
	 *
	 * @return the resources
	 */
	Iterable<Provider<InputStream>> getResources();

	/**
	 * Gets the names.
	 *
	 * @return the names
	 */
	Iterable<String> getNames();

	/**
	 * Gets the resource.
	 *
	 * @param name the name
	 * @return the resource
	 */
	Provider<InputStream> getResource(String name);
}
