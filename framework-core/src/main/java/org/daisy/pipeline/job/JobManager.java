package org.daisy.pipeline.job;



/**
 * The Interface JobManager offers a simple way of managing jobs.
 */
public interface JobManager {

	/**
	 * creates a job attached to the resource collection that will be used as context to the job.
	 *
	 * @param context the context for the new job
	 * @return the job
	 * @throws IllegalArgumentException if the jobId inside the context already exists in the manager
	 */
	public Job newJob(JobContext ctxt);


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
