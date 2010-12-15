package org.daisy.pipeline.xproc;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;

import org.xml.sax.EntityResolver;

public interface XProcessorFactory {

	 static final String DEFAULT_IMP="nl.dedicon.xproc.Impl.XProcFactoryImpl"; 
	
	//TODO build using class loader
	
	
	public abstract ErrorListener getErrorListener();

	
	public abstract  URIResolver getURIResolver();
	
	public abstract  EntityResolver getEntityResolver();

	
	public abstract void setErrorListener(ErrorListener errListener);
	

	
	public abstract void setURIResolver(URIResolver uriResolver);
	
	public abstract void setEntityResolver(EntityResolver entityResolver);
	
	public abstract void setSchemaAware(boolean schemaAware);
/*
	protected XProcessorFactory(){
		
	}
	
	public static XProcessorFactory newInstance(){
		return newInstance(DEFAULT_IMP, System.class.getClassLoader());
	}
	
	public static XProcessorFactory newInstance(String className,ClassLoader loader){
		try {
			Class<XProcessorFactory> clazz;
			if(loader!=null)
				clazz= (Class<XProcessorFactory>) loader.loadClass(className);
			else
				clazz = (Class<XProcessorFactory>) Class.forName(className);
			return (XProcessorFactory) clazz.getMethod("newInstance",new Class[]{}).invoke(new Object[]{});
			
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Could not load class:"+className+" using the class loader provided", e);
		} catch (Exception ex) {
			throw new RuntimeException("Error creating instance of "+className+" ", ex);
		}
		
		
	}
	*/
	
	public abstract XProcessor getProcessor(Source source);
		
	
	
	
	
	
	
	
}
