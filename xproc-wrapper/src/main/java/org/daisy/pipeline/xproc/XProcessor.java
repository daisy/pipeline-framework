package org.daisy.pipeline.xproc;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;

import org.xml.sax.EntityResolver;

public abstract class XProcessor {

	protected XProcessor(){}

	
	public abstract ErrorListener getErrorListener(); 
	
	public abstract URIResolver getURIResolver();
	
	public abstract EntityResolver getEntityResolver();
	
	public abstract void setErrorListener(ErrorListener listener);
	
	public abstract void setEntityResolver(EntityResolver resolver);

	public abstract void setSchemaAware();
	
	public abstract void setParameter(String port,String name,Object value);
	
	public abstract Object getParameter(String port,String name);
	
	public abstract void setOption(String name,Object value);
	
	public abstract Object getOption(String name);


	
	public abstract void setURIResolver(URIResolver resolver); 

	public abstract Iterable <String> getInputPorts();
	
	public abstract Iterable <String> getOutputPorts();
	
	public abstract void bindInputPort(String name,Source src);
	public abstract void bindOutputPort(String name,Result result);
	
	public abstract void run();
	
	
	
}
