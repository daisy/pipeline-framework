package org.daisy.pipeline.job;


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
