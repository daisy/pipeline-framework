package org.daisy.pipeline.modules.tracker;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.daisy.expath.parser.DefaultModuleBuilder;
import org.daisy.expath.parser.EXPathPackageParser;
import org.daisy.pipeline.modules.Component;
import org.daisy.pipeline.modules.Module;
import org.daisy.pipeline.modules.ModuleRegistry;
import org.daisy.pipeline.modules.ResourceLoader;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultModuleRegistry implements ModuleRegistry {

	private final class ModuleTracker implements BundleTrackerCustomizer {
		@Override
		public Object addingBundle(final Bundle bundle,
				final BundleEvent event) {
			Bundle result = null;
			URL url = bundle.getResource("expath-pkg.xml");
			if (url != null) {
				logger.trace("tracking '{}' <{}>",
						bundle.getSymbolicName(), url);

				Module module = mParser.parse(url,
						new DefaultModuleBuilder()
								.withLoader(new ResourceLoader() {

									public URL loadResource(
											String path) {

										// TODO: this is not
										// efficient at all, assure
										// to
										// load the whole path
										// while loading the bundle
										// Enumeration res =
										// bundle.findEntries("/",
										// path,
										// true);
										// if(res==null)
										// return null;
										// String completePath =
										// res.nextElement().toString();
										URL url = bundle
												.getResource(path);
										return url;
									}
								}));

				// System.out.println(module.getName());
				addModule(module);
				result = bundle;

			}

			// Finally
			return result;
		}

		@Override
		public void modifiedBundle(Bundle bundle,
				BundleEvent event, Object object) {
			// TODO reset module
		}

		@Override
		public void removedBundle(Bundle bundle, BundleEvent event,
				Object object) {
			logger.trace("removing bundle '{}' [{}] ",
					bundle.getSymbolicName(), event);
			// FIXME remove module
			// bundles.remove(bundle.getSymbolicName());
		}
	}

	private static final Logger logger = LoggerFactory
			.getLogger(DefaultModuleRegistry.class);

	private HashMap<URI, Module> componentsMap = new HashMap<URI, Module>();
	private HashSet<Module> modules = new HashSet<Module>();
	private EXPathPackageParser mParser;

	private BundleTracker tracker;

	public DefaultModuleRegistry() {
	}

	public void init(BundleContext context) {
		logger.trace("Activating module registry");
		tracker = new BundleTracker(context, Bundle.ACTIVE,
				new ModuleTracker());
		tracker.open();
		//TODO open the tracker in a separate thread ?
		// new Thread() {
		// @Override
		// public void run() {
		// tracker.open();
		// }
		// }.start();
		logger.trace("Module registry up");
	}

	public void close() {
		tracker.close();
	}

	public void setParser(EXPathPackageParser parser) {
		this.mParser = parser;
	}

	public Iterator<Module> iterator() {
		return modules.iterator();
	}

	public Module getModuleByComponent(URI uri) {
		return componentsMap.get(uri);
	}

	public Module resolveDependency(URI component, Module source) {
		// TODO check cache, otherwise delegate to resolver
		return null;
	}

	@Override
	public Iterable<URI> getComponents() {
		return componentsMap.keySet();
	}

	@Override
	public void addModule(Module module) {
		logger.debug("Registring module {}", module.getName());
		modules.add(module);
		for (Component component : module.getComponents()) {
			logger.debug("  - {}", component.getURI());
			componentsMap.put(component.getURI(), module);
		}
	}
}
