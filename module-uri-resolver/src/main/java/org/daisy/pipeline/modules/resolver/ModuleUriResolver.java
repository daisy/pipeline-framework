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

public class ModuleUriResolver implements URIResolver {
	private static Logger mLogger = LoggerFactory.getLogger(ModuleUriResolver.class);
	private ModuleRegistry mRegistry = null;

	public void activate(){
		mLogger.trace("Activating module URI resolver");
	}

	public void setModuleRegistry(ModuleRegistry reg) {
		mRegistry = reg;
	}

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
