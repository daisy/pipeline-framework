package org.daisy.pipeline.job;



/**
 * A factory for creating JobId objects.
 */
public class JobIdFactory {
	
	/**
	 * New id object
	 *
	 * @return the job id
	 */
	public static JobId newId(){
		//TODO: based on config
		return new JobUUIDGenerator().generateId();
	}
	
	/**
	 * New id using a string object as base 
	 *
	 * @param base the base
	 * @return the job id
	 */
	public static JobId newIdFromString(String base){
		//TODO: based on config
		return new JobUUIDGenerator().generateIdFromString(base);
	}
}
