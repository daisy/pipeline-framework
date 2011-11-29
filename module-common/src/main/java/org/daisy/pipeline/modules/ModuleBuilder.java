package org.daisy.pipeline.modules;

import java.net.URI;
import java.util.Collection;
import java.util.Map;

import org.daisy.pipeline.modules.Component;
import org.daisy.pipeline.modules.Module;
import org.daisy.pipeline.modules.ResourceLoader;

public interface ModuleBuilder {

	Module build();

	ModuleBuilder withName(String name);

	ModuleBuilder withLoader(ResourceLoader loader);

	ModuleBuilder withVersion(String version);

	ModuleBuilder withTitle(String title);

	ModuleBuilder withDependencies(
			Map<? extends String, ? extends String> dependencies);

	ModuleBuilder withDependency(String name, String version);

	ModuleBuilder withComponents(
			Collection<? extends Component> components);
	ModuleBuilder withEntities(
			Collection<? extends Entity> entities);

	ModuleBuilder withComponent(URI uri, String path);

}