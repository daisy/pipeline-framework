package org.daisy.pipeline.job;

import org.daisy.pipeline.job.JobIdGenerator.JobId;

public class JobIdFactory {
	public static JobId newId(){
		//TODO: based on config
		return new JobUUIDGenerator().generateId();
	}
	public static JobId newIdFromString(String base){
		//TODO: based on config
		return new JobUUIDGenerator().generateIdFromString(base);
	}
}
