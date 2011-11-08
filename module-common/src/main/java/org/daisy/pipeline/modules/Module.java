package org.daisy.pipeline.modules;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * Daisy pipeline module holds a set of components accesible via their uri, its name, version and dependencies.  
 */
public class Module {

	/** The name. */
	private String name;
	
	/** The version. */
	private String version;
	
	/** The title. */
	private String title;
	
	/** The dependencies. */
	private Map<String, String> dependencies;
	
	/** The components. */
	private HashMap<URI,Component> components = new HashMap<URI, Component>();

	/**
	 * Instantiates a new module.
	 *
	 * @param name the name
	 * @param version the version
	 * @param title the title
	 * @param dependencies the dependencies
	 * @param components the components
	 */
	public Module(String name, String version, String title,
			Map<String, String> dependencies, List<Component> components) {
		this.name = name;
		this.version = version;
		this.title = title;
		this.dependencies = dependencies;
		for (Component component:components ){
			component.setModule(this);
			this.components.put(component.getURI(), component);
		}
		
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Gets the dependencies.
	 *
	 * @return the dependencies
	 */
	public Map<String, String> getDependencies() {
		return dependencies;
	}

	/**
	 * Gets the components.
	 *
	 * @return the components
	 */
	public Iterable<Component> getComponents() {
		return components.values();
	}
	
	/**
	 * Gets the component identified by the given uri.
	 *
	 * @param uri the uri
	 * @return the component
	 */
	public Component getComponent(URI uri){
		return components.get(uri);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getName() + " [" + getVersion() + "]";
	}
}