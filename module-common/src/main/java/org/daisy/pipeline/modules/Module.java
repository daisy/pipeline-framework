package org.daisy.pipeline.modules;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Module {

	private String name;
	private String version;
	private String title;
	private Map<String, String> dependencies;
	private HashMap<URI,Component> components = new HashMap<URI, Component>();

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

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String getTitle() {
		return title;
	}

	public Map<String, String> getDependencies() {
		return dependencies;
	}

	public Iterable<Component> getComponents() {
		return components.values();
	}
	public Component getComponent(URI uri){
		return components.get(uri);
	}
	@Override
	public String toString() {
		return getName() + " [" + getVersion() + "]";
	}
}