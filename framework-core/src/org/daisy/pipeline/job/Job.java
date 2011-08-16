package org.daisy.pipeline.job;

import java.io.File;
import java.io.IOException;

import org.daisy.common.xproc.XProcEngine;
import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcPipeline;
import org.daisy.common.xproc.XProcResult;
import org.daisy.pipeline.script.XProcScript;

//TODO check thread safety
public class Job {

	public static enum Status {
		IDLE, RUNNING, DONE
	}

	public static Job newJob(XProcScript script, XProcInput input) {
		// TODO validate input
		return new Job(JobIdFactory.newId(), script, input, null);
	}

	public static Job newJob(XProcScript script, XProcInput input,
			ResourceCollection context) {
		// TODO check arguments
		JobId id = JobIdFactory.newId();
		// FIXME "common path"+id.toString
		File dataDir = new File(id.toString());
		try {
			IOBridge bridge = new IOBridge(dataDir);
			XProcInput resolvedInput = bridge.resolve(script, input, context);
			// TODO validate input
			return new Job(id, script, resolvedInput, dataDir);
		} catch (IOException e) {
			throw new RuntimeException("Error resolving pipeline info", e);
		}
	}

	private final JobId id;
	private final XProcInput input;
	private final XProcScript script;
	private XProcResult output;
	private JobResult results;
	private final File dataDir;
	private Status status = Status.IDLE;

	private Job(JobId id, XProcScript script, XProcInput input, File dataDir) {
		// TODO check arguments
		this.id = id;
		this.script = script;
		this.input = input;
		this.dataDir = dataDir;
	}
	
	public JobId getId(){
		return id;
	}
	
	public Status getStatus(){
		return status;
	}

	XProcResult getXProcOutput() {
		return null;
	}

	public void run(XProcEngine engine) {
		status = Status.RUNNING;
		// TODO use a pipeline cache
		XProcPipeline pipeline = engine.load(script.getURI());
		output = pipeline.run(input);
		status = Status.DONE;
		results = new JobResult();
	}

	public JobResult getResult() {
		return results;
	}

}
