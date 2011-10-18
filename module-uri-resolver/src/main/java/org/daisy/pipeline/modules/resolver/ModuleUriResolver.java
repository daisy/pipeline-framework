package org.daisy.pipeline.modules.resolver;

import java.net.URI;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;

import org.daisy.pipeline.modules.Module;
import org.daisy.pipeline.modules.ModuleRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;


/**
 * ModuleUriResolver resolves uris taking into account the components  from the modules loaded.  
 */
public class ModuleUriResolver implements URIResolver {
	
	/** The m logger. */
	private static Logger mLogger = LoggerFactory.getLogger(ModuleUriResolver.class);
	
	/** The m registry. */
	private ModuleRegistry mRegistry = null;

	/**
	 * Activate.
	 */
	public void activate(){
		mLogger.trace("Activating module URI resolver");
	}

	/**
	 * Sets the module registry.
	 *
	 * @param reg the new module registry
	 */
	public void setModuleRegistry(ModuleRegistry reg) {
		mRegistry = reg;
	}

	/* (non-Javadoc)
	 * @see javax.xml.transform.URIResolver#resolve(java.lang.String, java.lang.String)
	 */
	public Source resolve(String href, String base) {
		// System.out.println("Resolving:"+href);
		URI uhref = URI.create(href);
		Module mod = mRegistry.getModuleByComponent(uhref);
		
		if (mod == null) {
			mLogger.debug("No module found for uri:"+href);
			return null;
		}
		URI resource = mod.getComponent(uhref).getResource();
		if (resource == null) {
			mLogger.debug("No resource found in module "+mod.getName()+" for uri :"+href);
			return null;
		}
		SAXSource source = new SAXSource(new InputSource(resource.toString()));
		return source;
	}


}
