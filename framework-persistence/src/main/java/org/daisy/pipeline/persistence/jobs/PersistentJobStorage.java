package org.daisy.pipeline.persistence.jobs;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.JobContextFactory;
import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.job.JobStorage;
import org.daisy.pipeline.job.JobStorageProvider;
import org.daisy.pipeline.persistence.Database;
import org.daisy.pipeline.script.ScriptRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//FIXME: make sure that the weakreference approach is thread-safe, this is imporatant
public class PersistentJobStorage  implements JobStorage,JobStorageProvider{ 

	private static final Logger logger = LoggerFactory.getLogger(PersistentJobStorage.class);

	private Database db;
	private JobContextFactory ctxtFactory;
	//used as a set
	private Map<JobId,WeakReference<PersistentJob>> cache= new HashMap<JobId,WeakReference<PersistentJob>>();

		
	/**
	 *
	 */
	public PersistentJobStorage() {
		this.cache=Collections.synchronizedMap(this.cache);
	}

	public void setDatabase(Database db){
		this.db=db;
	}
	public void setRegistry(ScriptRegistry scriptRegistry){
		PersistentJobContext.setScriptRegistry(scriptRegistry);
	}
	public void setJobContextFactory(JobContextFactory ctxtFactory){
		PersistentJobContext.setJobContextFactory(ctxtFactory);
		this.ctxtFactory=ctxtFactory;
	}
	private void checkDatabase(){
		if (db==null){
			logger.warn("Database is null in persistent job storage");	
			throw new IllegalStateException("db is null");
		}
	}

	@Override
	public Iterator<Job> iterator() {
		checkDatabase();
		return PersistentJob.getAllJobs(this.db).iterator();
	}

	@Override
	public JobStorage provide() {
		return this;
	}

	@Override
	public Job add(Job job) {
		checkDatabase();
		logger.debug("Adding job to db:"+job.getContext().getId());
		PersistentJob pjob=new PersistentJob(job,db);
		this.ctxtFactory.configure((PersistentJobContext)pjob.getContext());
		db.addObject(pjob);	
		cache.put(pjob.getContext().getId(),new WeakReference<PersistentJob>(pjob));

		return pjob;
	}

	@Override
	public Job remove(JobId jobId) {
		checkDatabase();
		Job job=db.getEntityManager().find(PersistentJob.class,jobId.toString());
		if(job!=null){
			db.deleteObject(job);
			cache.remove(job.getContext().getId());
			logger.debug(String.format("Job with id %s deleted",jobId));
		}
		return job;
	}

	@Override
	public Job get(JobId id) {
		checkDatabase();
		PersistentJob job =null;
		if(this.cache.containsKey(id)){
			job=this.cache.get(id).get();
			if(job==null)
				this.cache.remove(id);
		}

		if(job==null)
			db.getEntityManager().find(PersistentJob.class,id.toString());

		if(job!=null){
			job.setDatabase(db);
			this.ctxtFactory.configure((PersistentJobContext)job.getContext());
		}
		return job; 
	}
	
}
