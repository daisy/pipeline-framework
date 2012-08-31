package org.daisy.pipeline.job;

import java.util.Properties;

import org.daisy.common.xproc.XProcEngine;
import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcMonitor;
import org.daisy.common.xproc.XProcPipeline;
import org.daisy.common.xproc.XProcResult;
import org.daisy.pipeline.script.XProcScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
//TODO check thread safety
/**
 * The Class Job defines the execution unit.
 */
public class Job {
	private static final Logger logger = LoggerFactory
			.getLogger(Job.class);
	/**
	 * The Enum Status.
	 */
	public static enum Status {

		/** The IDLE. */
		IDLE,
		/** The RUNNING. */
		RUNNING,
		/** The DONE. */
		DONE,
		ERROR
	}



	/** The id. */
	private final JobId id;

	/** The input. */
	private final XProcInput input;

	/** The script. */
	private final XProcScript script;

	//private XProcResult output;

	/** The results. */
	private JobResult results;

	/** The io bridge. */
	private final IOBridge ioBridge;

	/** The status. */
	private Status status = Status.IDLE;

	private final XProcMonitor monitor;

	/**
	 * Instantiates a new job.
	 *
	 * @param id
	 *            the id
	 * @param script
	 *            the script
	 * @param input
	 *            the input
	 * @param ioBridge
	 *            the io bridge
	 */
	Job(JobId id, XProcScript script, XProcInput input,
			IOBridge ioBridge,JobMonitor monitor) {
		// TODO check arguments
		this.id = id;
		this.script = script;
		this.input = input;
		this.ioBridge = ioBridge;
		this.monitor=monitor;
		this.results=new JobResult.Builder().withMessageAccessor(monitor.getMessageAccessor()).withZipFile(null).withLogFile(null).build();
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public JobId getId() {
		return id;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Gets the script.
	 *
	 * @return the script
	 */
	public XProcScript getScript() {
		return script;
	}

	/**
	 * Gets the x proc output.
	 *
	 * @return the x proc output
	 */
	XProcResult getXProcOutput() {
		return null;
	}

	/**
	 * Runs the job using the XProcEngine as script loader.
	 *
	 * @param engine the engine
	 */
	public void run(XProcEngine engine) {
		status = Status.RUNNING;
		// TODO use a pipeline cache
		XProcPipeline pipeline = engine.load(script.getURI());
		Properties props=new Properties();
		props.setProperty("JOB_ID", id.toString());
		try{
			pipeline.run(input,monitor,props);
			buildResults();
			status=Status.DONE;
		}catch(Exception e){
			logger.error("job finished with error state",e);
			//buildResults();
			status=Status.ERROR;
		}

	}
	private void buildResults() {
		JobResult.Builder builder = new JobResult.Builder();
		builder.withMessageAccessor(monitor.getMessageAccessor());
		builder.withLogFile(ioBridge.getLogFile());
		builder = (ioBridge != null) ? builder.withZipFile(ioBridge
				.zipOutput()) : builder;
		results = builder.build();
	}
	/**
	 * Gets the result.
	 *
	 * @return the result
	 */
	public JobResult getResult() {
		return results;
	}

	public XProcMonitor getMonitor(){
		return monitor;
	}

}
