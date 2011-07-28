package org.daisy.pipeline.xproc;

import java.util.Properties;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;

import org.xml.sax.EntityResolver;

// TODO: Auto-generated Javadoc
/**
 * The Class XProcessor is a general interface for interacting with xproc processors
 */
public abstract class XProcessor {

	/**
	 * Instantiates a new XProcessor. Protected only XProcessorFactory can instantiate Xproc 
	 */
	protected XProcessor(){}

	
	/**
	 * Gets the error listener.
	 *
	 * @return the error listener
	 */
	public abstract ErrorListener getErrorListener(); 
	
	/**
	 * Gets the uRI resolver.
	 *
	 * @return the uRI resolver
	 */
	public abstract URIResolver getURIResolver();
	
	/**
	 * Gets the entity resolver.
	 *
	 * @return the entity resolver
	 */
	public abstract EntityResolver getEntityResolver();
	
	/**
	 * Sets the error listener.
	 *
	 * @param listener the new error listener
	 */
	public abstract void setErrorListener(ErrorListener listener);
	
	/**
	 * Sets the entity resolver.
	 *
	 * @param resolver the new entity resolver
	 */
	public abstract void setEntityResolver(EntityResolver resolver);

	/**
	 * Sets the schema aware.
	 */
	public abstract void setSchemaAware();
	
	/**
	 * Sets the parameter port.
	 *
	 * @param paramPort the new parameter
	 */
	public abstract void addParameterPort(ParameterPort paramPort);
	
	/**
	 * Gets the parameter port using the port name
	 *
	 * @param port the port
	 * @return the parameter
	 */
	public abstract ParameterPort getParameter(String port);
	
	/**
	 * Sets a new option.
	 *
	 * @param option the new option
	 */
	public abstract void setOption(NamedValue option);
	
	/**
	 * Gets the option.
	 *
	 * @param name the name
	 * @return the option
	 */
	public abstract NamedValue getOption(String name);
	
	/**
	 * Sets the uRI resolver.
	 *
	 * @param resolver the new uRI resolver
	 */
	public abstract void setURIResolver(URIResolver resolver); 
	
	/**
	 * Gets the input ports.
	 *
	 * @return the input ports
	 */
	public abstract Iterable <InputPort> getInputPorts();
	
	/**
	 * Gets the output ports.
	 *
	 * @return the output ports
	 */
	public abstract Iterable <OutputPort> getOutputPorts();
	
	/**
	 * Binds an input port.
	 *
	 * @param inPort the in port
	 */
	public abstract void bindInputPort(InputPort inPort);
	
	/**
	 * Binds an output port.
	 *
	 * @param outPort the out port
	 */
	public abstract void bindOutputPort(OutputPort outPort);
	
	/**
	 * Sets the properties.
	 *
	 * @param properties the new properties
	 */
	public abstract void setProperties(Properties properties);
	
	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	public abstract Properties getProperties();

	/**
	 * Runs the xproc pipeline
	 */
	public abstract void run();
	
	
	
}
