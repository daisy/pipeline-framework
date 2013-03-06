package org.daisy.pipeline.job;

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


	/** The status. */
	private volatile Status status = Status.IDLE;

	
	protected JobContext ctxt;

	protected Job(JobContext ctxt) {
		this.ctxt=ctxt;
	}

	protected Job(JobContext ctxt,Status status) {
		this.ctxt=ctxt;
		this.status=status;
	}
	public static Job newJob(JobContext ctxt){
		Job job=new Job(ctxt);
		job.changeStatus(Status.IDLE);
		return job;
	}
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public final JobId getId() {
		return this.ctxt.getId();
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public final Status getStatus() {
		synchronized(this.status){
			return status;
		}
	}

	protected void setStatus(Status status){
		synchronized(this.status){
			this.status=status;
		}
	}
	/**
	 * Gets the ctxt for this instance.
	 *
	 * @return The ctxt.
	 */
	public final JobContext getContext() {
		return this.ctxt;
	}

	/**
	 * Gets the x proc output.
	 *
	 * @return the x proc output
	 */
	final XProcResult getXProcOutput() {
		return null;
	}

	protected synchronized final void changeStatus(Status to){
		this.status=to;
		if (this.ctxt!=null&&this.ctxt.getEventBus()!=null)
			this.ctxt.getEventBus().post(new StatusMessage.Builder().withJobId(this.getId()).withStatus(this.status).build());
		else
			logger.warn("I couldnt broadcast my change of status because"+((this.ctxt==null)? " the context ": " event bus ") + "is null");
		this.onStatusChanged(to);
	}

	/**
	 * Runs the job using the XProcEngine as script loader.
	 *
	 * @param engine the engine
	 */
	public synchronized final void run(XProcEngine engine) {
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
			XProcResult results = pipeline.run(this.ctxt.getInputs(),this.ctxt.getMonitor(),props);
			this.ctxt.writeResult(results);
			changeStatus( Status.DONE );
		}catch(Exception e){
			logger.error("job finished with error state",e);
			changeStatus( Status.ERROR);
		}

	}

	protected void onStatusChanged(Status newStatus){
		//for subclasses
	}

}
