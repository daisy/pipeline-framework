package org.daisy.expath.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.daisy.pipeline.modules.Component;
import org.daisy.pipeline.modules.Module;

public class ModuleBuilder {

	private String name;
	private String version;
	private String title;
	private Map<String, String> dependencies = new HashMap<String, String>();
	private List<Component> components = new ArrayList<Component>();

	public Module build() {
		return new Module(name, version, title, dependencies, components);
	}

	public void withName(String name) {
		this.name = name;
	}

	public void withVersion(String version) {
		this.version = version;
	}

	public void withTitle(String title) {
		this.title = title;
	}

	public void withDependencies(
			Map<? extends String, ? extends String> dependencies) {
		this.dependencies.putAll(dependencies);
	}

	public void withDependency(String name, String version) {
		dependencies.put(name, version);
	}

	public void withComponents(Collection<? extends Component> components) {
		this.components.addAll(components);
	}

	public void withComponent(Component component) {
		components.add(component);
	}

}
