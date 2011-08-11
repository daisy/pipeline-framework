package org.daisy.pipeline.job;

import java.io.File;
import java.util.UUID;

import org.daisy.common.xproc.XProcEngine;
import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcPipeline;
import org.daisy.common.xproc.XProcResult;
import org.daisy.pipeline.script.XProcScript;

public class Job {

	public static Job newJob(XProcScript script, XProcInput input) {
		// TODO validate input
		return new Job(UUID.randomUUID(), script, input, null);
	}

	public static Job newJob(XProcScript script, XProcInput input,
			ResourceCollection context) {
		UUID id = UUID.randomUUID();
		File dataDir = new File(id.toString());
		IOBridge bridge = new IOBridge(dataDir);
		XProcInput resolvedInput = bridge.resolve(script, input, context);
		// TODO validate input
		return new Job(id, script, resolvedInput, dataDir);
	}

	UUID id;
	XProcInput input;
	XProcScript script;
	XProcResult output;
	JobResult results;
	File dataDir;
	String status;

	private Job(UUID id, XProcScript script, XProcInput input, File dataDir) {

	}

	XProcResult getXProcOutput() {
		return null;
	}

	public void run(XProcEngine engine) {
		status = "running";
		XProcPipeline pipeline = engine.load(script.getURI());
		XProcResult output = pipeline.run(input);
		status = "done";
		results = new JobResult();
	}

	public JobResult getResult() {
		return results;
	}
}
