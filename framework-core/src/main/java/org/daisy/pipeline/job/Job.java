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
		/** The ERROR*/
		ERROR
	}



	/** The id. */
	private final JobId id;

	private final JobContext ctxt;
	/** The script. */
	private final XProcScript script;

	/** The output. */
	private XProcResult output;

	/** The results. */
	private JobResult results;

	/** The status. */
	private Status status = Status.IDLE;

	

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
	Job(XProcScript script, JobContext ctxt) {
		
		this.id = ctxt.getId();
		this.script = script;
		this.ctxt=ctxt;
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
			//output = pipeline.run(input,monitor,props);
			status=Status.DONE;
		}catch(Exception e){
			logger.error("job finished with error state",e);
			status=Status.ERROR;
		}


		JobResult.Builder builder = new JobResult.Builder();
//		builder.withMessageAccessor(monitor.getMessageAccessor());
//		builder.withLogFile(ioBridge.getLogFile());
//		builder = (ioBridge != null) ? builder.withZipFile(ioBridge
//				.zipOutput()) : builder;
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
		//return monitor;
		return null;
	}

}
