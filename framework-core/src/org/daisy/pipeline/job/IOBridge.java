package org.daisy.pipeline.job;

import java.io.File;

import org.daisy.commons.xproc.XProcInput;
import org.daisy.pipeline.script.XProcScript;
import org.daisy.commons.xproc.io.ResourceCollection;

public class IOBridge {

	public IOBridge(File dataDir) {
		// TODO Auto-generated constructor stub
	}

	public XProcInput resolve(XProcScript script, XProcInput input,
			ResourceCollection context) {
		// TODO
		// 0. extract context to data dir
		// 1. explore the script XProcPipelineInfo
		// 2. if sth missing in input, try to resolve using context
		// 3. if sth contextual in input, try to resolve using context
		// 4. create result input
		return null;
	}

}
