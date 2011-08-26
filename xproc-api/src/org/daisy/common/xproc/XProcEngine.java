package org.daisy.common.xproc;

import java.net.URI;
import java.util.Properties;

public interface XProcEngine {
	public static final String CONFIGURATION_FILE = "org.daisy.pipeline.xproc.configuration";

	
	XProcPipeline load(URI uri);
	XProcPipelineInfo getInfo(URI uri);
	XProcResult run(URI uri, XProcInput data);
	void setProperties(Properties properties);
}
