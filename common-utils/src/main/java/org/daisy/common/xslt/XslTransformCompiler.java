package org.daisy.common.xslt;

import java.io.InputStream;

import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.Configuration;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XsltCompiler;

import com.xmlcalabash.core.XProcConfiguration;

/**
 * Immutable XSLT compiler with an optional URIResolver.
 */
public class XslTransformCompiler {

	public XslTransformCompiler(Configuration config) {
		setConfiguration(config);
	}

	public XslTransformCompiler(XProcConfiguration config) {
		setConfiguration(config);
	}

	public XslTransformCompiler(Configuration config, URIResolver uriResolver) {
		setConfiguration(config);
		mURIresolver = uriResolver;
	}

	public XslTransformCompiler(XProcConfiguration config, URIResolver uriResolver) {
		setConfiguration(config);
		mURIresolver = uriResolver;
	}

	public CompiledStylesheet compileStylesheet(InputStream stylesheet)
	        throws SaxonApiException {

		CompiledStylesheet cs = new CompiledStylesheet(mXsltCompiler.compile(new StreamSource(
		        stylesheet)));

		if (mURIresolver != null)
			cs.setURIResolver(mURIresolver);

		return cs;
	}

	private void setConfiguration(Configuration config) {
		initCompiler(config);
	}

	private void setConfiguration(XProcConfiguration config) {
		initCompiler(config.getProcessor().getUnderlyingConfiguration());
	}

	private void initCompiler(Configuration config) {
		mXsltCompiler = new Processor(config).newXsltCompiler();
	}

	private URIResolver mURIresolver;
	private XsltCompiler mXsltCompiler;
}
