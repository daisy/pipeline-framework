package org.daisy.pipeline.job;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import java.util.concurrent.CopyOnWriteArrayList;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//TODO:Rethink this class...
public final class JobStorageFactory  {

	private static final Logger logger = LoggerFactory.getLogger(JobStorageFactory.class);

	private static final JobStorage DEFAULT_JOB_STORAGE= new JobStorage(){
		HashMap<JobId,Job> map= new HashMap<JobId,Job>(); 
		public Job add(Job job){
			this.map.put(job.getContext().getId(),job);
			return job;
		}

		public Job remove(JobId jobId){
			return this.map.remove(jobId);
		}

		public Job get(JobId id){
			return this.map.get(id);

		}

		public Iterator<Job> iterator(){
			return this.map.values().iterator();
		}

	};

	List<JobStorageProvider> providers= new CopyOnWriteArrayList<JobStorageProvider>();

	private static final JobStorageFactory INSTANCE=new JobStorageFactory();

	/**
	 * Constructs a new instance.
	 */
	public JobStorageFactory() {
	}

	public static JobStorageFactory getInstance(){
		synchronized(INSTANCE.providers){
			return INSTANCE;
		}
	}

	public synchronized static JobStorage getJobStorage(){
		if(INSTANCE.providers.size()==0){
			//the default storage is a hashmap
			logger.debug("Returning the default job storage");
			return DEFAULT_JOB_STORAGE;  
		}else{
			logger.debug("Returning the configured job storage");
			return INSTANCE.providers.get(INSTANCE.providers.size()-1).provide();
		}
	}

	protected synchronized static JobStorage getDefaultStorage(){
			return DEFAULT_JOB_STORAGE;  
	}

	public synchronized void add(JobStorageProvider  storageProvider){
		logger.debug("Adding storage provider "+storageProvider);
		INSTANCE.providers.add(storageProvider);
	}
	public synchronized boolean remove(JobStorageProvider  storageProvider){
		boolean done=INSTANCE.providers.remove(storageProvider);
		logger.debug(String.format( "Removing storage provider %s with result %s",storageProvider,done));
		return done;
	}

	@Override
	public String toString() {
		return String.format("JobStorageFactory:size[%d]",INSTANCE.providers.size());
	}

}
