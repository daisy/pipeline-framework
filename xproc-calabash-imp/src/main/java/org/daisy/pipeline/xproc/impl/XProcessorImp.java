package org.daisy.pipeline.xproc.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;

import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;

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

public class XProcessorImp extends XProcessor {

	private XPipeline mPipeline;
	private XProcRuntime mProcRuntime;
	private ErrorListener mErrorListener;

	private HashMap<String, Source> mInputPorts = new HashMap<String, Source>();
	private HashMap<String, Result> mOutputPorts = new HashMap<String, Result>();
	private XProcParameters mParams;
	private HashMap<String, Object> mOptions = new HashMap<String, Object>();
	Logger mLogger = LoggerFactory.getLogger(XProcessorImp.class);

	// TODO this field is not being used because
	private EntityResolver mEntityResolver;
	private Properties mProperties;

	public XProcessorImp(XPipeline pipeline, XProcRuntime runtime) {
		
		
		mProcRuntime = runtime;
		mPipeline = pipeline;
		for (String s : mPipeline.getInputs()) {
			mInputPorts.put(s, null);
		}
		for (String s : mPipeline.getOutputs()) {
			mOutputPorts.put(s, null);
		}
		
		mParams = new XProcParameters(mInputPorts.keySet());
		mProperties=new Properties();
		
	}

	@Override
	public ErrorListener getErrorListener() {
		return mErrorListener;
	}

	@Override
	public URIResolver getURIResolver() {

		return this.mProcRuntime.getResolver();
	}

	@Override
	public void setErrorListener(ErrorListener listener) {
		mErrorListener = listener;

	}

	@Override
	public void setSchemaAware() {

	}

	@Override
	public void setURIResolver(URIResolver resolver) {
		mProcRuntime.setURIResolver(resolver);

	}

	@Override
	public void run() {
		//let the processor crash, as errors are thrown if the input is defined inline
		//checkPorts();
		try {
			bindPorts();
		} catch (ClassCastException cce) {
			throw new RuntimeException(
					"Only SAX Sources are allowed at this moment:"
							+ cce.getMessage(), cce);
		}

		for (String port : mParams.getParametrizedPorts()) {
			for (String param : mParams.getParametersFromPort(port)) {
				mPipeline.setParameter(port, new QName(param),
						new RuntimeValue(mParams.getParameter(port, param)
								.toString()));
			}
		}
		
		for(String option:mOptions.keySet()){
			mPipeline.passOption(new QName(option),new RuntimeValue( mOptions.get(option).toString()));
		}
		checkOutPorts();
		//mProcRuntime.setMessageListener(new slf4jXProcMessageListener());
		
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

	private void bindResults() throws SaxonApiException {

		for (String port : mPipeline.getOutputs()) {
			Result result = mOutputPorts.get(port);
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

	private void bindPorts() {
		for (String port : mPipeline.getInputs()) {
			if (mInputPorts.get(port) != null) {
				Source src = (SAXSource) mInputPorts.get(port);
				XdmNode doc = mProcRuntime.parse(src.getSystemId(), "");
				mPipeline.writeTo(port, doc);
			}
		}

	}

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

	@Override
	public EntityResolver getEntityResolver() {
		return mEntityResolver;
	}

	@Override
	public void setEntityResolver(EntityResolver resolver) {
		mEntityResolver = resolver;
		this.mProcRuntime.setEntityResolver(mEntityResolver);

	}

	@Override
	public Iterable<String> getInputPorts() {
		return this.mInputPorts.keySet();
	}

	@Override
	public Iterable<String> getOutputPorts() {
		return this.mOutputPorts.keySet();
	}

	@Override
	public void bindInputPort(String name, Source src) {
		this.mInputPorts.put(name, src);
	}

	@Override
	public void bindOutputPort(String name, Result result) {
		this.mOutputPorts.put(name, result);

	}

	@Override
	public void setParameter(String port, String name, Object value) {
		mInputPorts.put(port, null);
		mParams.addParameter(port, name, value);
	}

	@Override
	public Object getParameter(String port, String name) {
		return mParams.getParameter(port, name);

	}

	@Override
	public void setOption(String name, Object value) {
		mOptions.put(name, value);
		
	}

	@Override
	public Object getOption(String name) {
		return mOptions.get(name);
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

class XProcParameters {

	HashMap<String, HashMap<String, Object>> mPorts = new HashMap<String, HashMap<String, Object>>();
	HashSet<String> mAllowedPorts = new HashSet<String>();

	public XProcParameters(Collection<String> ports) {
		mAllowedPorts.addAll(ports);
	}

	public void addParameter(String port, String parameter, Object value) {
		if (mAllowedPorts.contains(port)) {
			if (mPorts.get(port) == null) {
				mPorts.put(port, new HashMap<String, Object>());
			}
			mPorts.get(port).put(parameter, value);
		} else {
			throw new RuntimeException(
					"Unable to bind parameter,port not found:" + port);
		}

	}

	public Object getParameter(String port, String parameter) {
		if (mAllowedPorts.contains(port)) {
			if (mPorts.get(port) == null) {
				return null;
			}
			return mPorts.get(port).get(parameter);
		} else {
			throw new RuntimeException(
					"Unable to get parameter,port not found:" + port);
		}

	}

	public Set<String> getParametrizedPorts() {
		return mPorts.keySet();
	}

	public Set<String> getParametersFromPort(String port) {
		return mPorts.get(port).keySet();
	}

}