package org.daisy.pipeline.job;



// TODO: Auto-generated Javadoc
/**
 * A factory for creating JobId objects.
 */
public class JobIdFactory {

	/**
	 * New id object.
	 *
	 * @return the job id
	 */
	public static JobId newId(){
		//TODO: based on config
		return new JobUUIDGenerator().generateId();
	}
	/**
	 * New batch id object.
	 *
	 * @return the job batch id
	 */
	public static JobBatchId newBatchId(){
		return new JobUUIDGenerator().generateBatchId();
	}

	
	/**
	 * New id using a string object as base.
	 *
	 * @param base the base
	 * @return the job id
	 */
	public static JobId newIdFromString(String base){
		//TODO: based on config
		return new JobUUIDGenerator().generateIdFromString(base);
	}

	public static JobBatchId newBatchIdFromString(String base){
		//TODO: based on config
		return new JobUUIDGenerator().generateBatchIdFromString(base);
	}
}
