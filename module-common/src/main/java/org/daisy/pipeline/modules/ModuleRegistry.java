package org.daisy.pipeline.modules;

import java.net.URI;



/**
 * ModuleRegistry offers the functionality to enregister and query for the modules loaded.
 */
public interface ModuleRegistry extends Iterable<Module> {

	
	/**
	 * Adds the module.
	 *
	 * @param module the module
	 */
	public void addModule(Module module);
	
	/**
	 * Gets the module which has a component identified by the unique systemId.
	 *
	 * @param uri the uri
	 * @return the module by component
	 */
	public Module getModuleByComponent(URI uri);

	/**
	 * Gets the module handler which solves the dependency of the module source
	 * with the returned module component uri.
	 *
	 * @param component the component
	 * @param source the source
	 * @return the module
	 */
	public Module resolveDependency(URI component, Module source);
	
	/**
	 * Returns the list of available components.
	 *
	 * @return an iterable with all the available components
	 */
	public Iterable<URI> getComponents();

}