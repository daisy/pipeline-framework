package org.daisy.pipeline.executor;

import java.util.Properties;

import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;

import net.sf.saxon.Configuration;

import org.daisy.pipeline.modules.UriResolverDecorator;
import org.daisy.pipeline.modules.converter.Executor;
import org.daisy.pipeline.modules.converter.XProcRunnable;
import org.daisy.pipeline.xproc.InputPort;
import org.daisy.pipeline.xproc.NamedValue;
import org.daisy.pipeline.xproc.OutputPort;
import org.daisy.pipeline.xproc.ParameterPort;
import org.daisy.pipeline.xproc.XProcessor;
import org.daisy.pipeline.xproc.XProcessorFactory;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OSGIXprocExecutor implements Executor{

	private XProcessorFactory mXprocFactory = null; 
	private URIResolver mUriResolver = null;
	private Logger mLogger= LoggerFactory.getLogger(this.getClass());
	public static BundleContext ctxt=null;
	public static OSGIXprocExecutor instance=null;
	public void init(BundleContext ctxt){
				this.ctxt=ctxt;
				instance=this;
	}
	@Override
	public void execute(XProcRunnable runnable) {
		URIResolver defaultResolver = Configuration.newConfiguration().getURIResolver();
		((UriResolverDecorator)mUriResolver).setDelegatedUriResolver(defaultResolver);
		mXprocFactory.setURIResolver(mUriResolver);
		SAXSource src= new SAXSource();
		src.setSystemId(runnable.getPipelineUri().toString());
		XProcessor proc = mXprocFactory.getProcessor(src);

		for (InputPort inPort:runnable.getInputPorts())
			proc.bindInputPort(inPort);
		for (OutputPort outPort:runnable.getOutputPorts())
			proc.bindOutputPort(outPort);
		for (ParameterPort paramPort:runnable.getParamterPorts())
			proc.addParameterPort(paramPort);
		for (NamedValue option:runnable.getOptions())
			proc.setOption(option);
		proc.run();

		
	}
	
	public void setXprocFactory(XProcessorFactory xProcFactory){
		mXprocFactory=xProcFactory;
		Properties props = new Properties();
		if (System.getProperties().containsKey(XProcessorFactory.CONFIGURATION_FILE)){
			mLogger.debug("xproc configuration file set to:"+System.getProperty(XProcessorFactory.CONFIGURATION_FILE));
			props.setProperty(XProcessorFactory.CONFIGURATION_FILE, System.getProperty(XProcessorFactory.CONFIGURATION_FILE));
		}
		mXprocFactory.setProperties(props);
	}
	/**
	 * Sets the uri resolver.
	 *
	 * @param uriResolver the new uri resolver
	 */
	public void setUriResolver(UriResolverDecorator uriResolver) {
		
		mUriResolver = uriResolver;
	}
	
}
