package org.daisy.pipeline.job;



/**
 * DefaultJobManager allows to manage the jobs submitted to the daisy pipeline 2
 */
public abstract class AbstractJobManager implements JobManager {

	private JobStorageProvider provider;

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.job.JobManager#newJob(org.daisy.pipeline.script.XProcScript, org.daisy.common.xproc.XProcInput, org.daisy.pipeline.job.ResourceCollection)
	 */
	@Override
	public final Job newJob(JobContext ctxt) {
		this.checkProvider();
		if(this.getJob(ctxt.getId())!=null)
				throw new IllegalArgumentException(String.format("Job with id %s already exists in this manager",ctxt.getId()));
		//this part is quite critical, peristent storage needs to wrap the 
		//job object, so we MUST be sure that we return the right reference.
		//That's why newJob is final, if a concrete JobManager wants to do 
		//something to job will be done by "onJobCreated" 
		Job job = this.provider.provide().add(Job.newJob(ctxt));
		this.onNewJob(job);
		return job;
	}
	
	private void checkProvider(){
		if (this.provider==null) 
			throw new IllegalStateException("No JobStorageProvider in AbstractJobManager");
	}
	/**
	 * This method allows to do some after job creation hook-ups if needed.
	 */
	protected abstract void onNewJob(Job job);
	

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.job.JobManager#getJobIds()
	 */
	@Override
	public Iterable<Job> getJobs() {
		this.checkProvider();
		return this.provider.provide();
	}

	/**
	 * Deletes the job and cleans its context. If you are not using AbstractJobContexts 
	 * (you have implemented your own JobContexts) you should implement this class and 
	 * make a custom deletion, otherwise the context won't be cleared out.
	 * @see org.daisy.pipeline.job.JobManager#deleteJob(org.daisy.pipeline.job.JobId)
	 */
	@Override
	public Job deleteJob(JobId id) {
		this.checkProvider();
		Job job=this.getJob(id);
		this.provider.provide().remove(id);
		if (job!=null && job.getContext() instanceof AbstractJobContext){
			//clean the context 
			((AbstractJobContext)job.getContext()).cleanUp();
		}
		return job; 
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.job.JobManager#getJob(org.daisy.pipeline.job.JobId)
	 */
	@Override
	public Job getJob(JobId id) {
		this.checkProvider();
		return this.provider.provide().get(id);
	}

	public void setJobStorageProvider(JobStorageProvider provider){
		this.provider=provider;
	}
}
