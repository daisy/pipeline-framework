package org.daisy.calabash;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;

import com.xmlcalabash.core.XProcConfiguration;
import com.xmlcalabash.core.XProcRuntime;
import com.xmlcalabash.core.XProcStep;
import com.xmlcalabash.runtime.XAtomicStep;

public class DynamicXProcConfiguration extends XProcConfiguration {

	XProcStepRegistry stepRegistry;
	Logger mLogger = LoggerFactory.getLogger(getClass().getCanonicalName());
	public DynamicXProcConfiguration(XProcStepRegistry stepRegistry) {
		super();
		this.stepRegistry = stepRegistry;
	}

	public DynamicXProcConfiguration(boolean schemaAware,
			XProcStepRegistry stepRegistry) {
		super(schemaAware);
		this.stepRegistry = stepRegistry;
	}

	public DynamicXProcConfiguration(Processor processor,
			XProcStepRegistry stepRegistry) {
		super(processor);
		this.stepRegistry = stepRegistry;
	}

	
	public boolean isStepAvailable(QName type) {
		return stepRegistry.hasStep(type) || super.isStepAvailable(type); 
	}

	
	public XProcStep newStep(XProcRuntime runtime, XAtomicStep step) {
		
		if (step == null) {
			return null;
		} else {
			mLogger.info("getting step:"+step.getName());
			XProcStep xprocStep = stepRegistry.newStep(step.getType(), runtime,
					step);
			return (xprocStep != null) ? xprocStep : super.newStep(runtime,
					step);
		}
	}

}
