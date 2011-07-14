package org.daisy.pipeline.modules.tracker;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.daisy.expath.parser.EXPathPackageParser;
import org.daisy.pipeline.modules.Component;
import org.daisy.pipeline.modules.Module;
import org.daisy.pipeline.modules.ModuleRegistry;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultModuleRegistry implements ModuleRegistry {

	HashMap<URI, Module> mComponentsMap= new HashMap<URI, Module>();
	HashSet<Module> mModules= new HashSet<Module>();
	private EXPathPackageParser mParser;
	private Logger mLogger = LoggerFactory.getLogger(getClass());

	/*
	private final Function<Bundle, Module> toModule = new Function<Bundle, Module>() {

		public Module apply(Bundle bundle) {
		
			return parser.parse(bundle.getEntry("expath-pkg.xml"));
		}

	};
	 */
	private EXPathPackageTracker tracker;
	

	public DefaultModuleRegistry() {
	}

	public void init(BundleContext context) {
		tracker = new EXPathPackageTracker(context,this);
		tracker.setParser(mParser);
		tracker.open();
		mLogger.debug("Module registry up");

		
	}
	
	

	public void close() {
		tracker.close();
	}

	public void setParser(EXPathPackageParser parser) {
		this.mParser = parser;
	}

	public Iterator<Module> iterator() {
		return mModules.iterator();
	}

	public Module getModuleByComponent(URI uri) {
		return mComponentsMap.get(uri);
	}

	public Module resolveDependency(URI component, Module source) {
		// TODO check cache, otherwise delegate to resolver
		return null;
	}

	@Override
	public Iterable<URI> getComponents() {
		return mComponentsMap.keySet();
	}

	@Override
	public void addModule(Module module) {
		mModules.add(module);
		mLogger.debug("Registring "+module.getName());
		for(Component component: module.getComponents()){
			mLogger.debug("Component "+component.getURI());
			mComponentsMap.put(component.getURI(), module);
		}		
	}
}
