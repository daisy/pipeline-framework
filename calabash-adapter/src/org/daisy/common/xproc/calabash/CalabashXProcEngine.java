package org.daisy.common.xproc.calabash;

import java.net.URI;
import java.util.Properties;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;

import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;

import org.daisy.calabash.DynamicXProcConfigurationFactory;
import org.daisy.common.xproc.XProcEngine;
import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcPipeline;
import org.daisy.common.xproc.XProcPipelineInfo;
import org.daisy.common.xproc.XProcResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import com.xmlcalabash.core.XProcConfiguration;
import com.xmlcalabash.util.URIUtils;

public final class CalabashXProcEngine implements XProcEngine {
	public static final String CONFIGURATION_FILE = "org.daisy.pipeline.xproc.configuration";

	private final boolean mSchemaAware;
	private final ErrorListener mErrorListener;
	private final URIResolver mUriResolver;
	private final EntityResolver mEntityResolver;
	private final Properties mProperties;
	private final Logger mLogger = LoggerFactory
			.getLogger(CalabashXProcEngine.class);

	public CalabashXProcEngine(URIResolver uriResolver,
			ErrorListener errorListener, EntityResolver entityResolver,
			boolean schemaAware, Properties properties) {
		this.mUriResolver = uriResolver;
		this.mErrorListener = errorListener;
		this.mEntityResolver = entityResolver;
		this.mSchemaAware = schemaAware;
		this.mProperties = properties;
	}

	@Override
	public XProcPipeline load(URI uri) {

		XProcConfiguration conf = new DynamicXProcConfigurationFactory()
				.newConfiguration();

		try {
			loadConfigurationFile(conf);
		} catch (SaxonApiException e1) {
			throw new RuntimeException("error loading configuration file", e1);
		}

		conf.schemaAware = this.mSchemaAware;
		// try this with anonymous classes
		if (mErrorListener != null)
			conf.errorListener = this.mErrorListener.getClass().getName();

		return new CalabashXProcPipeline(uri, conf);
	}

	@Override
	public XProcPipelineInfo getInfo(URI uri) {
		return load(uri).getInfo();
	}

	@Override
	public XProcResult run(URI uri, XProcInput data) {
		return load(uri).run(data);
	}

	private void loadConfigurationFile(XProcConfiguration conf)
			throws SaxonApiException {
		// TODO cleanup
		if (mProperties.getProperty(CONFIGURATION_FILE) != null) {
			mLogger.debug("Reading configuration from "
					+ mProperties.getProperty(CONFIGURATION_FILE));
			// Make this absolute because sometimes it fails from the command
			// line otherwise. WTF?
			String cfgURI = URIUtils.cwdAsURI()
					.resolve(mProperties.getProperty(CONFIGURATION_FILE))
					.toASCIIString();
			SAXSource source = new SAXSource(new InputSource(cfgURI));
			DocumentBuilder builder = conf.getProcessor().newDocumentBuilder();
			XdmNode doc = builder.build(source);
			conf.parse(doc);
		}
	}

}
