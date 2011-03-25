package org.daisy.pipeline.modules.tracker;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.daisy.expath.parser.DefaultModuleBuilder;
import org.daisy.expath.parser.EXPathPackageParser;
import org.daisy.pipeline.modules.Module;
import org.daisy.pipeline.modules.ModuleRegistry;
import org.daisy.pipeline.modules.ResourceLoader;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.util.tracker.BundleTracker;

public class EXPathPackageTracker extends BundleTracker {

	private Map<String, Bundle> bundles = new HashMap<String, Bundle>();
	ModuleRegistry mRegistry;
	public EXPathPackageTracker(BundleContext context,ModuleRegistry reg) {
		super(context, Bundle.ACTIVE, null);
		mRegistry=reg;
	}
	private EXPathPackageParser mParser;
	
	@Override
	public Object addingBundle(final Bundle bundle, BundleEvent event) {
		//System.out.println("Adding bundle:"+bundle.getSymbolicName());
		Bundle result = null;
		URL url = bundle.getResource("expath-pkg.xml");
		
		if (url != null) {
			//System.out.println("tracking: " + bundle.getSymbolicName());
			Module module = mParser.parse(url, new DefaultModuleBuilder().withLoader(new ResourceLoader() {
				
				public URL loadResource(String path) {
				
					//TODO: this is not efficient at all, assure to load the whole path 
					//while loading the bundle 
					//Enumeration res = bundle.findEntries("/", path, true);
					//if(res==null)
						//return null;
					//String completePath = res.nextElement().toString();
					System.out.println("[PATH] "+path);
					URL url = bundle.getResource(path);
					return url;
				}
			}));
			mRegistry.addModule(module);
			result = bundle;
		}

		// Finally
		return result;
	}
	public void setParser(EXPathPackageParser parser) {
		this.mParser = parser;
	}
	@Override
	public void removedBundle(Bundle bundle, BundleEvent event, Object object) {
		super.removedBundle(bundle, event, object);
		System.out.println("removing: " + bundle.getSymbolicName() + "["
				+ event + "]");
		bundles.remove(bundle.getSymbolicName());
	}

}
