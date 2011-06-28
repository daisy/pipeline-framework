package org.daisy.pipeline.xproc.impl;

import java.util.Properties;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;

import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;

import org.daisy.calabash.DynamicXProcConfigurationFactory;
import org.daisy.calabash.XProcConfigurationFactory;
import org.daisy.pipeline.xproc.XProcessor;
import org.daisy.pipeline.xproc.XProcessorFactory;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import com.xmlcalabash.core.XProcConfiguration;
import com.xmlcalabash.core.XProcRuntime;
import com.xmlcalabash.runtime.XPipeline;
import com.xmlcalabash.util.URIUtils;

public class XProcFactoryImpl implements XProcessorFactory,Configurable{

	private ErrorListener mErrorListener=null; 
	private URIResolver mUriResolver = null;
	private EntityResolver mEntityResolver = null;
	private boolean mSchemaAware = false;
	private Properties mProperties=null;
	Logger mLogger = LoggerFactory.getLogger(XProcFactoryImpl.class);
	private XProcConfiguration mConfiguration;

	public XProcFactoryImpl(){
		mProperties=new Properties();
	}
	
	public void init(BundleContext context) {
	
		
	}
	
	
    public void setConfiguration(XProcConfigurationFactory conf){
    	this.mConfiguration = conf.newConfiguration();
    	mLogger.debug("configuration set via osgi");
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
		mUriResolver=arg0;
		
	}

	@Override
	public XProcessor getProcessor(Source source)  {
		XProcConfiguration conf=null;
		if (mConfiguration==null){
			conf = new DynamicXProcConfigurationFactory().newConfiguration();
			mLogger.warn("setting new configuration to calabash, this should be already set");
		}else{
			conf=mConfiguration;
			mLogger.debug("calabash configuration taken from state");
		}
			
		try {
			loadConfigurationFile(conf);
		} catch (SaxonApiException e1) {
			throw new RuntimeException("error loading configuration file",e1); 
		}
		conf.schemaAware=this.mSchemaAware;
		//try this with anonymous classes  
		if(mErrorListener!=null)
			conf.errorListener = this.mErrorListener.getClass().getName();
		XProcRuntime runtime = new XProcRuntime(conf);
		runtime.setMessageListener(new slf4jXProcMessageListener());
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

	private void loadConfigurationFile(XProcConfiguration conf) throws SaxonApiException {
	      if (mProperties.getProperty(CONFIGURATION_FILE) != null) {
	    	  mLogger.debug("Reading configuration from "+mProperties.getProperty(CONFIGURATION_FILE));
              // Make this absolute because sometimes it fails from the command line otherwise. WTF?
              String cfgURI = URIUtils.cwdAsURI().resolve(mProperties.getProperty(CONFIGURATION_FILE)).toASCIIString();
              SAXSource source = new SAXSource(new InputSource(cfgURI));
              DocumentBuilder builder = conf.getProcessor().newDocumentBuilder();
              XdmNode doc = builder.build(source);
              conf.parse(doc);
          }
		
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

	@Override
	public void setProperties(Properties properties) {
		mProperties=properties;
	}

	@Override
	public Properties getProperties() {
		return mProperties;

	}

}
