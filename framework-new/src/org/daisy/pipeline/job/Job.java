package org.daisy.pipeline.job;

import org.daisy.common.xproc.XProcEngine;
import org.daisy.common.xproc.XProcPipeline;
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

	public static Job newJob(XProcScript script, JobContext context){
		throw new UnsupportedOperationException();
	}

	private final JobId id;
	private final XProcScript script;
	private final JobContext context;
	private JobResult results;
	private Status status = Status.IDLE;


	private Job(JobId id, XProcScript script, JobContext context) {
		this.id = id;
		this.script = script;
		this.context = context;
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
	 * Runs the job using the XProcEngine as script loader.
	 *
	 * @param engine the engine
	 */
	public void run(XProcEngine engine) {
		status = Status.RUNNING;
		// TODO use a pipeline cache
		XProcPipeline pipeline = engine.load(script.getURI());
		try{
			output = pipeline.run(input,monitor);
			status=Status.DONE;
		}catch(Exception e){
			logger.error("job finished with error state",e);
			status=Status.ERROR;
		}


		JobResult.Builder builder = new JobResult.Builder();
		builder.withMessageAccessor(output.getMessages());
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

}
