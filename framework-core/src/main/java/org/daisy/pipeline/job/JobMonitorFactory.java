package org.daisy.pipeline.job;

public interface JobMonitorFactory {

	/**
	 * Get the monitor for a job.
	 *
	 * @param live Whether to create a "live" monitor (for monitoring the job while it runs) if not
	 *             already existing, or whether messages can be assumed to be fixed. Should be set
	 *             to true when the job is idle or running.
	 *
	 * If the live monitor is present in memory, always returns it. Otherwise loads the messages
	 * from storage.
	 */
	public JobMonitor newJobMonitor(JobId id, boolean live);

}
