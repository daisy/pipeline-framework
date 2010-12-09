package org.daisy.pipeline.modules;

import java.util.List;
import java.util.Map;

public class Module {

	private String name;
	private String version;
	private String title;
	private Map<String, String> dependencies;
	private List<Component> components;

	public Module(String name, String version, String title,
			Map<String, String> dependencies, List<Component> components) {
		this.name = name;
		this.version = version;
		this.title = title;
		this.dependencies = dependencies;
		this.components = components;
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

	public List<Component> getComponents() {
		return components;
	}

	@Override
	public String toString() {
		return getName() + " [" + getVersion() + "]";
	}
}