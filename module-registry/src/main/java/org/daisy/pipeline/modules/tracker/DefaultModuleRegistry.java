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


/**
 * The Class DefaultModuleRegistry tracks the modules loaded into the pipeline.
 */
public class DefaultModuleRegistry implements ModuleRegistry {

	/**
	 * ModuleTracker tracks bundles loaded into the OSGI framework, recognises the daisy pipeline 2 modules and stores them. 
	 */
	private final class ModuleTracker implements BundleTrackerCustomizer {
		
		/* (non-Javadoc)
		 * @see org.osgi.util.tracker.BundleTrackerCustomizer#addingBundle(org.osgi.framework.Bundle, org.osgi.framework.BundleEvent)
		 */
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

		/* (non-Javadoc)
		 * @see org.osgi.util.tracker.BundleTrackerCustomizer#modifiedBundle(org.osgi.framework.Bundle, org.osgi.framework.BundleEvent, java.lang.Object)
		 */
		@Override
		public void modifiedBundle(Bundle bundle,
				BundleEvent event, Object object) {
			// TODO reset module
		}

		/* (non-Javadoc)
		 * @see org.osgi.util.tracker.BundleTrackerCustomizer#removedBundle(org.osgi.framework.Bundle, org.osgi.framework.BundleEvent, java.lang.Object)
		 */
		@Override
		public void removedBundle(Bundle bundle, BundleEvent event,
				Object object) {
			logger.trace("removing bundle '{}' [{}] ",
					bundle.getSymbolicName(), event);
			// FIXME remove module
			// bundles.remove(bundle.getSymbolicName());
		}
	}

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory
			.getLogger(DefaultModuleRegistry.class);

	/** The components map. */
	private HashMap<URI, Module> componentsMap = new HashMap<URI, Module>();
	
	/** The modules. */
	private HashSet<Module> modules = new HashSet<Module>();
	
	/** The m parser. */
	private EXPathPackageParser mParser;

	/** The tracker. */
	private BundleTracker tracker;

	/**
	 * Instantiates a new default module registry.
	 */
	public DefaultModuleRegistry() {
	}

	/**
	 * Inits the the registry (OSGI)
	 *
	 * @param context the context
	 */
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

	/**
	 * Closes the registr (OSGI)
	 */
	public void close() {
		tracker.close();
	}

	/**
	 * Sets the expath parser to read the module's information
	 *
	 * @param parser the new parser
	 */
	public void setParser(EXPathPackageParser parser) {
		this.mParser = parser;
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<Module> iterator() {
		return modules.iterator();
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.modules.ModuleRegistry#getModuleByComponent(java.net.URI)
	 */
	public Module getModuleByComponent(URI uri) {
		return componentsMap.get(uri);
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.modules.ModuleRegistry#resolveDependency(java.net.URI, org.daisy.pipeline.modules.Module)
	 */
	public Module resolveDependency(URI component, Module source) {
		// TODO check cache, otherwise delegate to resolver
		return null;
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.modules.ModuleRegistry#getComponents()
	 */
	@Override
	public Iterable<URI> getComponents() {
		return componentsMap.keySet();
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.modules.ModuleRegistry#addModule(org.daisy.pipeline.modules.Module)
	 */
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
