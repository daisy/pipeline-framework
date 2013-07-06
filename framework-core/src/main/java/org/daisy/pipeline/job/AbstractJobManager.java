package org.daisy.pipeline.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;




/**
 * DefaultJobManager allows to manage the jobs submitted to the daisy pipeline 2
 */
public abstract class AbstractJobManager implements JobManager {

	private static final Logger logger = LoggerFactory.getLogger(AbstractJobManager.class);

	private JobStorage storage;

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.job.JobManager#newJob(org.daisy.pipeline.script.XProcScript, org.daisy.common.xproc.XProcInput, org.daisy.pipeline.job.ResourceCollection)
	 */
	@Override
	public final Job newJob(JobContext ctxt) {
		this.checkStorage();
		if(this.getJob(ctxt.getId())!=null)
				throw new IllegalArgumentException(String.format("Job with id %s already exists in this manager",ctxt.getId()));
		//this part is quite critical, peristent storage needs to wrap the 
		//job object, so we MUST be sure that we return the right reference.
		//That's why newJob is final, if a concrete JobManager wants to do 
		//something to job will be done by "onNewJob" 
		Job job = this.storage.add(ctxt);
		this.onNewJob(job);
		return job;
	}
	
	private void checkStorage(){
		if (this.storage==null) 
			throw new IllegalStateException("No JobStorage in AbstractJobManager");
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
		this.checkStorage();
		return this.storage;
	}

	/**
	 * Deletes the job and cleans its context. If you are not using AbstractJobContexts 
	 * (you have implemented your own JobContexts) you should implement this class and 
	 * make a custom deletion, otherwise the context won't be cleared out.
	 * @see org.daisy.pipeline.job.JobManager#deleteJob(org.daisy.pipeline.job.JobId)
	 */
	@Override
	public Job deleteJob(JobId id) {
		this.checkStorage();
		Job job=this.getJob(id);
		this.storage.remove(id);
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
		this.checkStorage();
		return this.storage.get(id);
	}

	public void setJobStorage(JobStorage storage){
		this.storage=storage;
	}

	@Override
	public void deleteAll() {
		this.checkStorage();
		logger.info("deleting all jobs");
		//iterate over a copy of the jobs, to make sure
		//that we clean the context up
		for (Job job:Iterables.toArray(this.storage,Job.class)){
			logger.debug(String.format("Deleting job %s",job));
			((AbstractJobContext)job.getContext()).cleanUp();
			this.storage.remove(job.getId());
		}
		
	}

	//public void deactivate(){
		//String cleanUp=System.getProperty("org.daisy.pipeline.cleanJobs","");
		//if(cleanUp.equalsIgnoreCase("true")){
			//logger.info("deactivate AbstractJobManager: cleaning jobs");
	//}
	//}
}
