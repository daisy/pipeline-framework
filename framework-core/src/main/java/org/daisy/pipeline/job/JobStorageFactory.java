package org.daisy.pipeline.job;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.daisy.common.base.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JobStorageFactory  {

	private static final Logger logger = LoggerFactory.getLogger(JobStorageFactory.class);

	private static final JobStorage DEFAULT_JOB_STORAGE= new JobStorage(){
		HashMap<JobId,Job> map= new HashMap<JobId,Job>(); 
		public void add(Job job){
			this.map.put(job.getContext().getId(),job);
		}

		public Job remove(JobId jobId){
			return this.map.remove(jobId);
		}

		public Job get(JobId id){
			return this.map.get(id);

		}

		public Iterator<JobId> iterator(){
			return this.map.keySet().iterator();
		}

	};

	List<Provider<JobStorage>> providers= new LinkedList<Provider<JobStorage>>();

	private static final JobStorageFactory INSTANCE=new JobStorageFactory();

	/**
	 * Constructs a new instance.
	 */
	private JobStorageFactory() {
	}

	public static JobStorageFactory getInstance(){
		return INSTANCE;
	}

	public synchronized static JobStorage getJobStorage(){
		if(INSTANCE.providers.size()==0){
			//the default storage is a hashmap
			return DEFAULT_JOB_STORAGE;  
		}else{
			return INSTANCE.providers.get(INSTANCE.providers.size()-1).provide();
		}
	}

	protected synchronized static JobStorage getDefaultStorage(){
			return DEFAULT_JOB_STORAGE;  
	}

	public void add(Provider<JobStorage>  storageProvider){
		logger.debug("Adding storage provider "+storageProvider);
		providers.add(storageProvider);
	}
	public boolean remove(Provider<JobStorage>  storageProvider){
		boolean done=providers.remove(storageProvider);
		logger.debug(String.format( "Removing storage provider %s with result %s",storageProvider,done));
		return done;
	}

	@Override
	public String toString() {
		return String.format("JobStorageFactory:size[%d]",this.providers.size());
	}

}
