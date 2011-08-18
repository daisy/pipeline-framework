package org.daisy.common.xproc.calabash;

import java.net.URI;
import java.util.Properties;

import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;

import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;

import org.daisy.calabash.XProcConfigurationFactory;
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

//TODO check thread safety
public final class CalabashXProcEngine implements XProcEngine {
	public static final String CONFIGURATION_FILE = "org.daisy.pipeline.xproc.configuration";

	private static final Logger logger = LoggerFactory
			.getLogger(CalabashXProcEngine.class);

	private boolean schemaAware = false;
	private URIResolver uriResolver = null;
	private EntityResolver entityResolver = null;
	private Properties properties = null;
	private XProcConfigurationFactory configFactory = null;

	public CalabashXProcEngine() {
	}

	@Override
	public XProcPipeline load(URI uri) {
		// TODO check that the dynamic config factory is set
		XProcConfiguration conf = configFactory != null ? configFactory
				.newConfiguration(schemaAware) : new XProcConfiguration(
				schemaAware);

		try {
			loadConfigurationFile(conf);
		} catch (SaxonApiException e1) {
			throw new RuntimeException("error loading configuration file", e1);
		}

		conf.schemaAware = this.schemaAware;

		return new CalabashXProcPipeline(uri, conf, uriResolver, entityResolver);
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
		if (properties != null
				&& properties.getProperty(CONFIGURATION_FILE) != null) {
			logger.debug("Reading configuration from "
					+ properties.getProperty(CONFIGURATION_FILE));
			// Make this absolute because sometimes it fails from the command
			// line otherwise. WTF?
			String cfgURI = URIUtils.cwdAsURI()
					.resolve(properties.getProperty(CONFIGURATION_FILE))
					.toASCIIString();
			SAXSource source = new SAXSource(new InputSource(cfgURI));
			DocumentBuilder builder = conf.getProcessor().newDocumentBuilder();
			XdmNode doc = builder.build(source);
			conf.parse(doc);
		}
	}

	public void setConfigurationFactory(XProcConfigurationFactory configFactory) {
		this.configFactory = configFactory;
	}

	public void setEntityResolver(EntityResolver entityResolver) {
		this.entityResolver = entityResolver;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public void setSchemaAware(boolean schemaAware) {
		this.schemaAware = schemaAware;
	}

	public void setUriResolver(URIResolver uriResolver) {
		this.uriResolver = uriResolver;
	}

}
