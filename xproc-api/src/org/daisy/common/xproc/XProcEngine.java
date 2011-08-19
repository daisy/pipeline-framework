package org.daisy.common.xproc;

import java.net.URI;

public interface XProcEngine {
	XProcPipeline load(URI uri);
	XProcPipelineInfo getInfo(URI uri);
	XProcResult run(URI uri, XProcInput data);
}
