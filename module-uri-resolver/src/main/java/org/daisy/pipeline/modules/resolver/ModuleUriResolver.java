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
import org.xml.sax.InputSource;

public class ModuleUriResolver implements UriResolverDecorator {
	ModuleRegistry mRegistry = null;
	private URIResolver mDelegated;
	public void init(BundleContext ctxt){
		
	}
	public void stop(){
		
	}
	
	
	public void setModuleRegistry(ModuleRegistry reg){
		mRegistry=reg;
	}
	public Source resolve(String href, String base) {
		URI uhref= URI.create(href);
		Module mod = mRegistry.getModuleByComponent(uhref);
		if(mod==null){
			try {
				return mDelegated.resolve(href, base);
			} catch (TransformerException e) {
				return null;
			}
		}
		URI resource = mod.getComponent(uhref).getResource();
		if(resource==null){
			try {
				return mDelegated.resolve(href, base);
			} catch (TransformerException e) {
				return null;
			}
		}
		SAXSource source = new SAXSource(new InputSource(resource.toString()));
		return source;
	}
	@Override
	public UriResolverDecorator setDelegatedUriResolver(URIResolver uriResolver) {
		mDelegated=uriResolver;
		return this;
	}

}
