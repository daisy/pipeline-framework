package org.daisy.pipeline.job;



/**
 * DefaultJobManager allows to manage the jobs submitted to the daisy pipeline 2
 */
public abstract class AbstractJobManager implements JobManager {

	/** The jobs. */

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.job.JobManager#newJob(org.daisy.pipeline.script.XProcScript, org.daisy.common.xproc.XProcInput, org.daisy.pipeline.job.ResourceCollection)
	 */
	@Override
	public Job newJob(JobContext ctxt) {
		if(this.getJob(ctxt.getId())!=null)
				throw new IllegalArgumentException(String.format("Job with id %s already exists in this manager",ctxt.getId()));
		Job job = JobStorageFactory.getJobStorage().add(Job.newJob(ctxt));
		return job;
	}


	/* (non-Javadoc)
	 * @see org.daisy.pipeline.job.JobManager#getJobIds()
	 */
	@Override
	public Iterable<Job> getJobs() {
		return JobStorageFactory.getJobStorage();
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.job.JobManager#deleteJob(org.daisy.pipeline.job.JobId)
	 */
	@Override
	public Job deleteJob(JobId id) {
		//jobs.get(id).cleanUp();
		return JobStorageFactory.getJobStorage().remove(id); 
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.job.JobManager#getJob(org.daisy.pipeline.job.JobId)
	 */
	@Override
	public Job getJob(JobId id) {
		return JobStorageFactory.getJobStorage().get(id);
	}
}
