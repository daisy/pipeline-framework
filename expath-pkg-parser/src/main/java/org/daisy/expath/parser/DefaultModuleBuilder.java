package org.daisy.expath.parser;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.daisy.pipeline.modules.Component;
import org.daisy.pipeline.modules.Module;
import org.daisy.pipeline.modules.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultModuleBuilder implements ModuleBuilder {

	private ResourceLoader loader;
	private String name;
	private String version;
	private String title;
	private Map<String, String> dependencies = new HashMap<String, String>();
	private List<Component> components = new ArrayList<Component>();
	private Logger mLogger = LoggerFactory.getLogger(getClass());
	public Module build() {
		return new Module(name, version, title, dependencies, components);
	}

	public ModuleBuilder withName(String name) {
		this.name = name;
		return this;
	}

	public ModuleBuilder withLoader(ResourceLoader loader) {
		this.loader = loader;
		return this;
	}

	public ModuleBuilder withVersion(String version) {
		this.version = version;
		return this;
	}

	public ModuleBuilder withTitle(String title) {
		this.title = title;
		return this;
	}

	public ModuleBuilder withDependencies(
			Map<? extends String, ? extends String> dependencies) {
		this.dependencies.putAll(dependencies);
		return this;
	}

	public ModuleBuilder withDependency(String name, String version) {
		dependencies.put(name, version);
		return this;
	}

	public ModuleBuilder withComponents(
			Collection<? extends Component> components) {
		this.components.addAll(components);
		return this;
	}

	public ModuleBuilder withComponent(URI uri, String path) {
		mLogger.trace("withComponent:"+uri.toString()+", path: "+path);
		components.add(new Component(uri, path,  loader));
		return this;
	}

}
