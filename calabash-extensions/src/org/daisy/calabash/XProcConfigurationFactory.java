package org.daisy.calabash;

import net.sf.saxon.s9api.Processor;

import com.xmlcalabash.core.XProcConfiguration;


public interface XProcConfigurationFactory {
	XProcConfiguration newConfiguration();

	XProcConfiguration newConfiguration(boolean schemaAware);

	XProcConfiguration newConfiguration(Processor processor);
}
