package org.daisy.common.xproc.calabash;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.sax.SAXSource;

import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import com.xmlcalabash.core.XProcConfiguration;
import com.xmlcalabash.core.XProcRuntime;
import com.xmlcalabash.core.XProcStep;
import com.xmlcalabash.runtime.XAtomicStep;
import com.xmlcalabash.util.URIUtils;

public class DynamicXProcConfigurationFactory implements
		XProcConfigurationFactory, XProcStepRegistry {

	public static final String CONFIG_PATH = "org.daisy.pipeline.xproc.configuration";

	private static final Logger logger = LoggerFactory
			.getLogger(DynamicXProcConfigurationFactory.class);

	private Map<QName, XProcStepProvider> stepProviders = new HashMap<QName, XProcStepProvider>();

	public XProcConfiguration newConfiguration() {
		XProcConfiguration config = new DynamicXProcConfiguration(this);
		loadConfigurationFile(config);
		return config;
	}
	
	public void activate(){
		logger.trace("Activating XProc Configuration Factory");
	}

	public XProcConfiguration newConfiguration(boolean schemaAware) {
		XProcConfiguration config = new DynamicXProcConfiguration(schemaAware, this);
		loadConfigurationFile(config);
		return config;
	}

	public XProcConfiguration newConfiguration(Processor processor) {
		XProcConfiguration config = new DynamicXProcConfiguration(processor, this);
		loadConfigurationFile(config);
		return config;
	}

	public void addStep(XProcStepProvider stepProvider, Map<?, ?> properties) {
		QName type = QName.fromClarkName((String) properties.get("type"));
		logger.debug("Adding step to registry: {}", type.toString());
		stepProviders.put(type, stepProvider);
	}

	public void removeStep(XProcStepProvider stepProvider, Map<?, ?> properties) {
		QName type = QName.fromClarkName((String) properties.get("type"));
		logger.debug("Removing step from registry: {}", type.toString());
 		stepProviders.remove(type);
	}

	public boolean hasStep(QName type) {
		return stepProviders.containsKey(type);
	}

	public XProcStep newStep(QName type, XProcRuntime runtime, XAtomicStep step) {
		XProcStepProvider stepProvider = stepProviders.get(type);
		return (stepProvider != null) ? stepProvider.newStep(runtime, step)
				: null;
	}

	private void loadConfigurationFile(XProcConfiguration conf) {
		// TODO cleanup and cache
		String configPath = System.getProperty(CONFIG_PATH);
		if (configPath != null) {
			logger.debug("Reading Calabash configuration from {}", configPath);
			// Make this absolute because sometimes it fails from the command
			// line otherwise. WTF?
			String cfgURI = URIUtils.cwdAsURI().resolve(configPath)
					.toASCIIString();
			SAXSource source = new SAXSource(new InputSource(cfgURI));
			DocumentBuilder builder = conf.getProcessor().newDocumentBuilder();
			XdmNode doc;
			try {
				doc = builder.build(source);
			} catch (SaxonApiException e) {
				throw new RuntimeException("error loading configuration file",
						e);
			}
			conf.parse(doc);
		}
	}
}
