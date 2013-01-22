package org.daisy.pipeline.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import org.daisy.common.xproc.XProcEngine;
import org.daisy.common.xproc.XProcPipeline;
import org.daisy.common.xproc.XProcResult;

import org.daisy.pipeline.job.StatusMessage;
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


	/** The results. */
	private JobResult results;

	/** The status. */
	protected Status status = Status.IDLE;

	
	protected JobContext ctxt;

	protected Job(JobContext ctxt) {
		this.ctxt=ctxt;
		//this.results=new JobResult.Builder().withMessageAccessor(this.ctxt.getMonitor().getMessageAccessor()).withZipFile(null).withLogFile(null).build();
		changeStatus(Status.IDLE);
	}
	public static Job newJob(JobContext ctxt){
		return new Job(ctxt);
	}
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public JobId getId() {
		return this.ctxt.getId();
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
	 * Gets the ctxt for this instance.
	 *
	 * @return The ctxt.
	 */
	public JobContext getContext() {
		return this.ctxt;
	}

	/**
	 * Gets the x proc output.
	 *
	 * @return the x proc output
	 */
	XProcResult getXProcOutput() {
		return null;
	}

	protected void changeStatus(Status to){
		this.status=to;
		//TODO clean this
		if (this.ctxt!=null&&this.ctxt.getEventBus()!=null)
			this.ctxt.getEventBus().post(new StatusMessage.Builder().withJobId(this.getId()).withStatus(this.status).build());
		//else
		//	logger.warn("I couldnt broadcast my change of status because"+((this.ctxt==null)? " the context ": " event bus ") + "is null");
	}
	/**
	 * Runs the job using the XProcEngine as script loader.
	 *
	 * @param engine the engine
	 */
	public void run(XProcEngine engine) {
		changeStatus(Status.RUNNING);
		XProcPipeline pipeline = null;
		try{
		pipeline = engine.load(this.ctxt.getScript().getURI());
		}catch (Exception e){
			logger.error("Error while loading the script:"+this.ctxt.getScript().getName());
			throw new RuntimeException(e);
		}
		try{
			Properties props=new Properties();
			props.setProperty("JOB_ID", this.ctxt.getId().toString());
			pipeline.run(this.ctxt.getInputs(),this.ctxt.getMonitor(),props);
			buildResults();
			changeStatus( Status.DONE );
		}catch(Exception e){
			logger.error("job finished with error state",e);
			//buildResults();
			changeStatus( Status.ERROR);
		}

	}
	private void buildResults() {
		//TODO: manage results
		//JobResult.Builder builder = new JobResult.Builder();
		//builder.withMessageAccessor(this.ctxt.getMonitor().getMessageAccessor());
		//builder.withLogFile(this.ctxt.getLogFile());
		//builder = (ioBridge != null) ? builder.withZipFile(ioBridge
				//.zipOutput()) : builder;
		//results = builder.build();
		results=null;
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
