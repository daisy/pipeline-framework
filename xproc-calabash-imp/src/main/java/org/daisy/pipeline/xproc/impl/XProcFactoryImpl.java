package org.daisy.pipeline.xproc.impl;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;

import net.sf.saxon.s9api.SaxonApiException;

import org.daisy.pipeline.xproc.XProcessor;
import org.daisy.pipeline.xproc.XProcessorFactory;
import org.osgi.framework.BundleContext;
import org.xml.sax.EntityResolver;

import com.xmlcalabash.core.XProcConfiguration;
import com.xmlcalabash.core.XProcRuntime;
import com.xmlcalabash.runtime.XPipeline;

public class XProcFactoryImpl implements XProcessorFactory{

	private ErrorListener mErrorListener=null; 
	private URIResolver mUriResolver = null;
	private EntityResolver mEntityResolver = null;
	private boolean mSchemaAware = false;
	
	public XProcFactoryImpl(){
		
	}
	
	public void init(BundleContext context) {
	
		
	}
	
	

	public void close() {
	}
	
	public static XProcessorFactory newInstance(){
		return new XProcFactoryImpl();
		
	}
	@Override
	public ErrorListener getErrorListener() {
		return mErrorListener;
	}

	@Override
	public URIResolver getURIResolver() {
		return mUriResolver;
	}

	@Override
	public void setErrorListener(ErrorListener errListener) {
		mErrorListener=errListener;
		
	}

	@Override
	public void setURIResolver(URIResolver arg0) {
		mUriResolver=mUriResolver;
		
	}

	@Override
	public XProcessor getProcessor(Source source)  {
		
		
		XProcConfiguration conf = new XProcConfiguration();
		conf.schemaAware=this.mSchemaAware;
		//try this with anonymous classes  
		if(mErrorListener!=null)
			conf.errorListener = this.mErrorListener.getClass().getName();
		XProcRuntime runtime = new XProcRuntime(conf);
		if(mUriResolver!=null)
			runtime.setURIResolver(mUriResolver);
		if(mEntityResolver!=null)
			runtime.setEntityResolver(mEntityResolver);
		
		XPipeline pipeline = null;
		
		//TODO: this is going to work only with those Sources which contains the uri inside the systemId
		//at some point add functionality with DOMSource, 
		String uri = source.getSystemId();
		if(uri==null){
			throw new IllegalArgumentException("At the moment only Sources with a uri as systemId are allowed");
		}
		
		try {
			pipeline=runtime.load(uri);
		} catch (SaxonApiException e) {
			throw new RuntimeException(e.getMessage(),e);
		}
		
		XProcessor inst = new XProcessorImp(pipeline,runtime);
		inst.setEntityResolver(mEntityResolver);
		inst.setErrorListener(mErrorListener);
		return inst;
	}

	@Override
	public void setSchemaAware(boolean schemaAware) {
		mSchemaAware = schemaAware;
		
	}

	@Override
	public EntityResolver getEntityResolver() {
		
		return mEntityResolver;
	}

	@Override
	public void setEntityResolver(EntityResolver entityResolver) {
		this.mEntityResolver=entityResolver;
		
	}

}
