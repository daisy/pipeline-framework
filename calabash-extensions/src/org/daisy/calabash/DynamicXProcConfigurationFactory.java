package org.daisy.calabash;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;

import com.xmlcalabash.core.XProcConfiguration;
import com.xmlcalabash.core.XProcRuntime;
import com.xmlcalabash.core.XProcStep;
import com.xmlcalabash.runtime.XAtomicStep;

public class DynamicXProcConfigurationFactory implements
		XProcConfigurationFactory, XProcStepRegistry {
	Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private Map<QName, XProcStepProvider> stepProviders = new HashMap<QName, XProcStepProvider>();
	
	public XProcConfiguration newConfiguration() {
		return new DynamicXProcConfiguration(this);
	}

	public XProcConfiguration newConfiguration(boolean schemaAware) {
		return new DynamicXProcConfiguration(schemaAware, this);
	}

	public XProcConfiguration newConfiguration(Processor processor) {
		return new DynamicXProcConfiguration(processor, this);
	}

	public void addStep(XProcStepProvider stepProvider, Map<?, ?> properties) {
		QName type = QName.fromClarkName((String) properties.get("type"));
		logger.info("Adding step to registry: "+type.toString());
		stepProviders.put(type, stepProvider);
	}

	public void removeStep(XProcStepProvider stepProvider, Map<?, ?> properties) {
		QName type = QName.fromClarkName((String) properties.get("type"));
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
    public void init(BundleContext ctxt){
    	logger.info(this.getClass().getName()+" is up");
    	
    }
}
