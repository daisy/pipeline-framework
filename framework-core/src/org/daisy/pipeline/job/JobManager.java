package org.daisy.pipeline.job;

import org.daisy.common.xproc.XProcInput;
import org.daisy.pipeline.script.XProcScript;


/**
 * The Interface JobManager offers a simple way of managing jobs 
 */
public interface JobManager {

	/**
	 * creates a job attached to the resource collection that will be used as context to the job.
	 *
	 * @param script the script
	 * @param input the input
	 * @param context the context
	 * @return the job
	 */
	public Job newJob(XProcScript script, XProcInput input,
			ResourceCollection context);

	/**
	 * Creates a job without resource collection
	 *
	 * @param script the script
	 * @param input the input
	 * @return the job
	 */
	public Job newJob(XProcScript script, XProcInput input);

	/**
	 * Gets the jobs.
	 *
	 * @return the jobs
	 */
	public Iterable<Job> getJobs();

	/**
	 * Deletes a job.
	 *
	 * @param id the id
	 * @return the job
	 */
	public Job deleteJob(JobId id);

	/**
	 * Gets the job.
	 *
	 * @param id the id
	 * @return the job
	 */
	public Job getJob(JobId id);
}
