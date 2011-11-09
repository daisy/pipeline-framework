package org.daisy.expath.parser;

import java.net.URI;
import java.util.Collection;
import java.util.Map;

import org.daisy.pipeline.modules.Component;
import org.daisy.pipeline.modules.Module;
import org.daisy.pipeline.modules.ResourceLoader;


/**
 * ModuleBuilder builds modules.
 */
public interface ModuleBuilder {

	/**
	 * Builds the module
	 *
	 * @return the module
	 */
	Module build();

	/**
	 * With name.
	 *
	 * @param name the name
	 * @return the module builder
	 */
	ModuleBuilder withName(String name);

	/**
	 * With loader.
	 *
	 * @param loader the loader
	 * @return the module builder
	 */
	ModuleBuilder withLoader(ResourceLoader loader);

	/**
	 * With version.
	 *
	 * @param version the version
	 * @return the module builder
	 */
	ModuleBuilder withVersion(String version);

	/**
	 * With title.
	 *
	 * @param title the title
	 * @return the module builder
	 */
	ModuleBuilder withTitle(String title);

	/**
	 * With dependencies.
	 *
	 * @param dependencies the dependencies
	 * @return the module builder
	 */
	ModuleBuilder withDependencies(
			Map<? extends String, ? extends String> dependencies);

	/**
	 * With dependency.
	 *
	 * @param name the name
	 * @param version the version
	 * @return the module builder
	 */
	ModuleBuilder withDependency(String name, String version);

	/**
	 * With components.
	 *
	 * @param components the components
	 * @return the module builder
	 */
	ModuleBuilder withComponents(
			Collection<? extends Component> components);

	/**
	 * With component.
	 *
	 * @param uri the uri
	 * @param path the path
	 * @return the module builder
	 */
	ModuleBuilder withComponent(URI uri, String path);

}