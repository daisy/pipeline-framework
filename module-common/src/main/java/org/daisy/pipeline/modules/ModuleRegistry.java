package org.daisy.pipeline.modules;

import java.net.URI;


public interface ModuleRegistry extends Iterable<Module> {

	
	public void addModule(Module module);
	/**
	 * Gets the module which has a component identified by the unique systemId
	 * 
	 * @param uri
	 * @return
	 */
	public Module getModuleByComponent(URI uri);

	/**
	 * Gets the module handler which solves the dependency of the module source
	 * with the returned module component uri
	 * 
	 * @param component
	 * @param source
	 * @return
	 */
	public Module resolveDependency(URI component, Module source);
	/**
	 * Returns the list of available components 
	 *  
	 * @return an iterable with all the available components  
	 */
	public Iterable<URI> getComponents();

}