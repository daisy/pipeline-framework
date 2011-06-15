package org.daisy.pipeline.modules.resolver;

import java.net.URI;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;

import org.daisy.pipeline.modules.Module;
import org.daisy.pipeline.modules.ModuleRegistry;
import org.daisy.pipeline.modules.UriResolverDecorator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

public class ModuleUriResolver implements UriResolverDecorator {
	ModuleRegistry mRegistry = null;
	private URIResolver mDelegated;
	Logger mLogger = LoggerFactory.getLogger(getClass().getName());

	public void init(BundleContext ctxt) {

	}

	public void stop() {

	}

	public void setModuleRegistry(ModuleRegistry reg) {
		mRegistry = reg;
	}

	public Source resolve(String href, String base) {
		// System.out.println("Resolving:"+href);
		URI uhref = URI.create(href);
		Module mod = mRegistry.getModuleByComponent(uhref);
		
		if (mod == null) {
			mLogger.info("No module found for uri:"+href);
			return delegate(href, base);
		}
		URI resource = mod.getComponent(uhref).getResource();
		if (resource == null) {
			mLogger.info("No resource found in module "+mod.getName()+" for uri :"+href);
			return delegate(href, base);
		}
		SAXSource source = new SAXSource(new InputSource(resource.toString()));
		return source;
	}

	public Source delegate(String href, String base) {
		if (mDelegated != null) {
			try {
				return mDelegated.resolve(href, base);
			} catch (TransformerException e) {
				return null;
			}
		} else {
			mLogger.warn("delegate resolver is null");
			
			return null;
		}
	}

	@Override
	public UriResolverDecorator setDelegatedUriResolver(URIResolver uriResolver) {
		mDelegated = uriResolver;
		return this;
	}

}
