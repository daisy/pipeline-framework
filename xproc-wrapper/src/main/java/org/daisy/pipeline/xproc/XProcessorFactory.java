package org.daisy.pipeline.xproc;

import java.util.Properties;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;

import org.xml.sax.EntityResolver;

public interface XProcessorFactory {

	 static final String DEFAULT_IMP="nl.dedicon.xproc.Impl.XProcFactoryImpl";
	 public static final String CONFIGURATION_FILE="CONFIGURAION_FILE";
	

	
	
	public abstract ErrorListener getErrorListener();
	public abstract  URIResolver getURIResolver();
	public abstract  EntityResolver getEntityResolver();
	public abstract void setErrorListener(ErrorListener errListener);
	public abstract void setURIResolver(URIResolver uriResolver);
	public abstract void setEntityResolver(EntityResolver entityResolver);
	public abstract void setSchemaAware(boolean schemaAware);
	public abstract XProcessor getProcessor(Source source);
	public abstract void setProperties(Properties properties);
	public abstract Properties getProperties();
	
		
	
	
	
	
	
	
	
}
