package org.daisy.pipeline.persistence.jobs;

import java.util.Iterator;

import org.daisy.common.base.Provider;

import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.job.JobStorage;
import org.daisy.pipeline.persistence.Database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistentJobStorage  implements JobStorage,Provider<JobStorage>{ 

	private static final Logger logger = LoggerFactory.getLogger(PersistentJobStorage.class);

	Database db;

	public void setDatabase(Database db){
		this.db=db;
	}

	private void checkDatabase(){
		if (db==null){
			logger.warn("Database is null in persistent job storage");	
			throw new IllegalStateException("db is null");
		}
	}

	@Override
	public Iterator<JobId> iterator() {
		checkDatabase();
		return PersistentJob.getAllJobIds(this.db).iterator();
	}

	@Override
	public JobStorage provide() {
		return this;
	}

	@Override
	public void add(Job job) {
		checkDatabase();
		logger.debug("Adding job to db:"+job.getContext().getId());
		db.addObject(new PersistentJob(job));	
	}

	@Override
	public Job remove(JobId jobId) {
		checkDatabase();
		Job job=db.getEntityManager().find(PersistentJob.class,jobId.toString());
		if(job!=null){
			db.deleteObject(job);
			logger.debug(String.format("Job with id %s deleted",jobId));
		}
		return job;
	}

	@Override
	public Job get(JobId id) {
		checkDatabase();
		return db.getEntityManager().find(PersistentJob.class,id.toString());
	}
	
}
