/*
 * 
 */
package org.daisy.pipeline.xproc.impl;

import java.util.HashMap;
import java.util.Properties;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;

import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;

import org.daisy.pipeline.xproc.InputPort;
import org.daisy.pipeline.xproc.NamedValue;
import org.daisy.pipeline.xproc.OutputPort;
import org.daisy.pipeline.xproc.ParameterPort;
import org.daisy.pipeline.xproc.XProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.EntityResolver;

import com.xmlcalabash.core.XProcRuntime;
import com.xmlcalabash.io.ReadablePipe;
import com.xmlcalabash.io.WritablePipe;
import com.xmlcalabash.model.RuntimeValue;
import com.xmlcalabash.model.Serialization;
import com.xmlcalabash.runtime.XPipeline;

// TODO: Auto-generated Javadoc
/**
 * The Class XProcessorImp is a calabash implementation for the XProc class
 */
public class XProcessorImp extends XProcessor {

	/** The Pipeline. */
	private XPipeline mPipeline;
	
	/** The Proc runtime. */
	private XProcRuntime mProcRuntime;
	
	/** The Error listener. */
	private ErrorListener mErrorListener;

	/** The Input ports. */
	private HashMap<String, InputPort> mInputPorts = new HashMap<String, InputPort>();
	
	/** The Output ports. */
	private HashMap<String, OutputPort> mOutputPorts = new HashMap<String, OutputPort>();
	
	/** The Options. */
	private HashMap<String,NamedValue> mOptions = new HashMap<String,NamedValue>();
	
	/** The Logger. */
	private Logger mLogger = LoggerFactory.getLogger(XProcessorImp.class);
	
	/** The Entity resolver. */
	private EntityResolver mEntityResolver;
	
	/** The Properties. */
	private Properties mProperties;
	
	/** The Params. */
	private HashMap<String, ParameterPort> mParams = new HashMap<String, ParameterPort>();
	




	/**
	 * Instantiates a new x processor imp.
	 *
	 * @param pipeline the pipeline
	 * @param runtime the runtime
	 */
	public XProcessorImp(XPipeline pipeline, XProcRuntime runtime) {
		mProcRuntime = runtime;
		mPipeline = pipeline;
		mProperties=new Properties();
		
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.xproc.XProcessor#getErrorListener()
	 */
	@Override
	public ErrorListener getErrorListener() {
		return mErrorListener;
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.xproc.XProcessor#getURIResolver()
	 */
	@Override
	public URIResolver getURIResolver() {

		return this.mProcRuntime.getResolver();
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.xproc.XProcessor#setErrorListener(javax.xml.transform.ErrorListener)
	 */
	@Override
	public void setErrorListener(ErrorListener listener) {
		mErrorListener = listener;

	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.xproc.XProcessor#setSchemaAware()
	 */
	@Override
	public void setSchemaAware() {

	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.xproc.XProcessor#setURIResolver(javax.xml.transform.URIResolver)
	 */
	@Override
	public void setURIResolver(URIResolver resolver) {
		mProcRuntime.setURIResolver(resolver);

	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.xproc.XProcessor#run()
	 */
	@Override
	public void run() {
		
		try {
			bindPorts();
		} catch (ClassCastException cce) {
			throw new RuntimeException(
					"Only SAX Sources are allowed at this moment:"
							+ cce.getMessage(), cce);
		}

		for (ParameterPort port : mParams.values()) {
			for (NamedValue param : port.getBinds()) {
				mPipeline.setParameter(port.getName(), new QName(param.getName()),
						new RuntimeValue(param.getValue()));
			}
		}
		
		for(NamedValue option:mOptions.values()){
			mPipeline.passOption(new QName(option.getName()),new RuntimeValue( option.getValue()));
		}

		
		try {
			
			mPipeline.run();
		} catch (SaxonApiException e) {
			throw new RuntimeException(e);
		}

		try {
			bindResults();
		} catch (SaxonApiException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Bind results.
	 *
	 * @throws SaxonApiException the saxon api exception
	 */
	private void bindResults() throws SaxonApiException {

		for (String port : mPipeline.getOutputs()) {
			//TODO sequential output
			Result result = mOutputPorts.get(port).getBinds().poll();
			ReadablePipe rpipe = mPipeline.readFrom(port);

			Serialization serial = new Serialization(mProcRuntime, mPipeline
					.getNode()); // The node's a hack
			for (String name : mProcRuntime.getConfiguration().serializationOptions
					.keySet()) {
				String value = mProcRuntime.getConfiguration().serializationOptions
						.get(name);

				if ("byte-order-mark".equals(name))
					serial.setByteOrderMark("true".equals(value));
				if ("escape-uri-attributes".equals(name))
					serial.setEscapeURIAttributes("true".equals(value));
				if ("include-content-type".equals(name))
					serial.setIncludeContentType("true".equals(value));
				if ("indent".equals(name))
					serial.setIndent("true".equals(value));
				if ("omit-xml-declaration".equals(name))
					serial.setOmitXMLDeclaration("true".equals(value));
				if ("undeclare-prefixes".equals(name))
					serial.setUndeclarePrefixes("true".equals(value));
				if ("method".equals(name))
					serial.setMethod(new QName("", value));

				// FIXME: if ("cdata-section-elements".equals(name))
				// serial.setCdataSectionElements();
				if ("doctype-public".equals(name))
					serial.setDoctypePublic(value);
				if ("doctype-system".equals(name))
					serial.setDoctypeSystem(value);
				if ("encoding".equals(name))
					serial.setEncoding(value);
				if ("media-type".equals(name))
					serial.setMediaType(value);
				if ("normalization-form".equals(name))
					serial.setNormalizationForm(value);
				if ("standalone".equals(name))
					serial.setStandalone(value);
				if ("version".equals(name))
					serial.setVersion(value);
			}
			
			WritablePipe wd= WritableFactory.getWritable(result, mProcRuntime, serial);
			while (rpipe.moreDocuments()) {
				wd.write(rpipe.read());
			}
		}

	}

	/**
	 * Binds  the input ports.
	 */
	private void bindPorts() {
		for (String port : mPipeline.getInputs()) {
			if (mInputPorts.containsKey(port)) {
				for (Source src:mInputPorts.get(port).getBinds()){
					XdmNode doc = mProcRuntime.parse(src.getSystemId(), "");
					mPipeline.writeTo(port, doc);
				}
			}
		}

	}
/*
	private void checkInPorts() {
		for (String s : mPipeline.getInputs()) {
			if (mInputPorts.get(s) == null && !mParams.getParametrizedPorts().contains(s))
				throw new RuntimeException("Unbound input port:" + s);
		}

		

	}
	
	private void checkOutPorts() {
		for (String s : mPipeline.getOutputs()) {
			if (mOutputPorts.get(s) == null)
				throw new RuntimeException("Unbound output port:" + s);
		}
	}
*/
	/* (non-Javadoc)
 * @see org.daisy.pipeline.xproc.XProcessor#getEntityResolver()
 */
@Override
	public EntityResolver getEntityResolver() {
		return mEntityResolver;
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.xproc.XProcessor#setEntityResolver(org.xml.sax.EntityResolver)
	 */
	@Override
	public void setEntityResolver(EntityResolver resolver) {
		mEntityResolver = resolver;
		this.mProcRuntime.setEntityResolver(mEntityResolver);

	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.xproc.XProcessor#getInputPorts()
	 */
	@Override
	public Iterable<InputPort> getInputPorts() {
		return this.mInputPorts.values();
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.xproc.XProcessor#getOutputPorts()
	 */
	@Override
	public Iterable<OutputPort> getOutputPorts() {
		return this.mOutputPorts.values();
	}

	
	
	
	
	/* (non-Javadoc)
	 * @see org.daisy.pipeline.xproc.XProcessor#getOption(java.lang.String)
	 */
	@Override
	public NamedValue getOption(String name) {
		return mOptions.get(name);
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.xproc.XProcessor#setProperties(java.util.Properties)
	 */
	@Override
	public void setProperties(Properties properties) {
		mProperties=properties;
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.xproc.XProcessor#getProperties()
	 */
	@Override
	public Properties getProperties() {
		return mProperties;
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.xproc.XProcessor#addParameterPort(org.daisy.pipeline.xproc.ParameterPort)
	 */
	@Override
	public void addParameterPort(ParameterPort paramPort) {
		this.mParams.put(paramPort.getName(), paramPort);
		
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.xproc.XProcessor#getParameter(java.lang.String)
	 */
	@Override
	public ParameterPort getParameter(String port) {
		return mParams.get(port);
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.xproc.XProcessor#setOption(org.daisy.pipeline.xproc.NamedValue)
	 */
	@Override
	public void setOption(NamedValue option) {
		mOptions.put(option.getKey(),option);
		
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.xproc.XProcessor#bindInputPort(org.daisy.pipeline.xproc.InputPort)
	 */
	@Override
	public void bindInputPort(InputPort inPort) {
		mInputPorts.put(inPort.getName(), inPort);
		
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.xproc.XProcessor#bindOutputPort(org.daisy.pipeline.xproc.OutputPort)
	 */
	@Override
	public void bindOutputPort(OutputPort outPort) {
		mOutputPorts.put(outPort.getName(), outPort);
	}

}

