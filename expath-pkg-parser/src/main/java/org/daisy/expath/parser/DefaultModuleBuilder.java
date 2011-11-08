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


/**
 * Builds modules from the given elements
 */
public class DefaultModuleBuilder implements ModuleBuilder {

	/** The loader. */
	private ResourceLoader loader;
	
	/** The name. */
	private String name;
	
	/** The version. */
	private String version;
	
	/** The title. */
	private String title;
	
	/** The dependencies. */
	private Map<String, String> dependencies = new HashMap<String, String>();
	
	/** The components. */
	private List<Component> components = new ArrayList<Component>();
	
	/** The m logger. */
	private Logger mLogger = LoggerFactory.getLogger(getClass());
	
	/* (non-Javadoc)
	 * @see org.daisy.expath.parser.ModuleBuilder#build()
	 */
	public Module build() {
		return new Module(name, version, title, dependencies, components);
	}

	/* (non-Javadoc)
	 * @see org.daisy.expath.parser.ModuleBuilder#withName(java.lang.String)
	 */
	public ModuleBuilder withName(String name) {
		this.name = name;
		return this;
	}

	/* (non-Javadoc)
	 * @see org.daisy.expath.parser.ModuleBuilder#withLoader(org.daisy.pipeline.modules.ResourceLoader)
	 */
	public ModuleBuilder withLoader(ResourceLoader loader) {
		this.loader = loader;
		return this;
	}

	/* (non-Javadoc)
	 * @see org.daisy.expath.parser.ModuleBuilder#withVersion(java.lang.String)
	 */
	public ModuleBuilder withVersion(String version) {
		this.version = version;
		return this;
	}

	/* (non-Javadoc)
	 * @see org.daisy.expath.parser.ModuleBuilder#withTitle(java.lang.String)
	 */
	public ModuleBuilder withTitle(String title) {
		this.title = title;
		return this;
	}

	/* (non-Javadoc)
	 * @see org.daisy.expath.parser.ModuleBuilder#withDependencies(java.util.Map)
	 */
	public ModuleBuilder withDependencies(
			Map<? extends String, ? extends String> dependencies) {
		this.dependencies.putAll(dependencies);
		return this;
	}

	/* (non-Javadoc)
	 * @see org.daisy.expath.parser.ModuleBuilder#withDependency(java.lang.String, java.lang.String)
	 */
	public ModuleBuilder withDependency(String name, String version) {
		dependencies.put(name, version);
		return this;
	}

	/* (non-Javadoc)
	 * @see org.daisy.expath.parser.ModuleBuilder#withComponents(java.util.Collection)
	 */
	public ModuleBuilder withComponents(
			Collection<? extends Component> components) {
		this.components.addAll(components);
		return this;
	}

	/* (non-Javadoc)
	 * @see org.daisy.expath.parser.ModuleBuilder#withComponent(java.net.URI, java.lang.String)
	 */
	public ModuleBuilder withComponent(URI uri, String path) {
		mLogger.trace("withComponent:"+uri.toString()+", path: "+path);
		components.add(new Component(uri, path,  loader));
		return this;
	}

}
