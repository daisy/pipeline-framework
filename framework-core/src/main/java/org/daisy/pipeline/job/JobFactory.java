package org.daisy.pipeline.job;

import org.daisy.pipeline.script.BoundXProcScript;

import com.google.common.base.Optional;

/**
 * Interface for creating jobs that are not associated with a client and that are not automatically
 * added to the storage and the execution queue.
 */
public interface JobFactory {

	/**
	 * Creates a job for the given script with bound inputs and outputs.
	 */
	public JobBuilder newJob(BoundXProcScript boundScript);

	public interface JobBuilder {
		public JobBuilder isMapping(boolean mapping);
		public JobBuilder withResources(JobResources resources);
		public JobBuilder withNiceName(String niceName);
		/**
		 * Request to automatically close the job when the object is dismissed or the virtual
		 * machine terminates.
		 */
		public JobBuilder closeOnExit();
		public Optional<Job> build();
	}
}
