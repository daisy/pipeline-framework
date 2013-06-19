package org.daisy.pipeline.nonpersistent.jobs;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.daisy.pipeline.event.EventBusProvider;
import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.JobContext;
import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.job.JobStorage;

import com.google.common.base.Function;
import com.google.common.eventbus.EventBus;

public class VolatileJobStorage implements JobStorage {
//	private static final Logger logger = LoggerFactory.getLogger(VolatileJobStorage.class);

	private Map<JobId,Job> jobs = Collections.synchronizedMap(new HashMap<JobId,Job>()); 
	private EventBus bus;


	public void setEventBusProvider(EventBusProvider busProvider){
		this.bus=busProvider.get();
	}

	@Override
	public Iterator<Job> iterator() {
		return this.jobs.values().iterator();
	}

	@Override
	public synchronized Job add(final JobContext ctxt) {
		if(!this.jobs.containsKey(ctxt.getId())){
			//Store the job before its status gets broadcasted
			Job job= new Job.JobBuilder()
				.withEventBus(this.bus)
				.withContext(ctxt)
				.build(new Function<Job, Job>() {
					@Override
					public Job apply(Job job) {
						VolatileJobStorage.this.jobs.put(ctxt.getId(),job);
						return job;
					}
				});
			return job;
		}
		return null;
	}

	@Override
	public synchronized Job remove(JobId jobId) {
		return this.jobs.remove(jobId);
		
	}

	@Override
	public synchronized Job get(JobId jobId) {
		return this.jobs.get(jobId);
	}
	
}
