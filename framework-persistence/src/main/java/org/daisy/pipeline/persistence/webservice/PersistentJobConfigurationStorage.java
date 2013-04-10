package org.daisy.pipeline.persistence.webservice;

import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.persistence.Database;
import org.daisy.pipeline.webserviceutils.storage.JobConfigurationStorage;

/**
 * This class implements a job storage
 * relying on JPA
 *
 * @author
 */
public class PersistentJobConfigurationStorage implements
		JobConfigurationStorage {

	private Database database;

	/**
	 * @param database
	 */
	public PersistentJobConfigurationStorage(Database database) {
		this.database = database;
	}

	@Override
	public void add(JobId id, String configuration) {
		PersistentJobConfiguration conf= new PersistentJobConfiguration.Builder()
			.withId(id).withConfiguration(configuration).build();
		this.database.addObject(conf);
	}

	@Override
	public String get(JobId id) {
		PersistentJobConfiguration cnf=
			database.getEntityManager().find(PersistentJobConfiguration.class,id.toString());
		if(cnf!=null){
			return cnf.getConfiguration();
		}else{
			return "";
		}
	}

	@Override
	public boolean delete(JobId id) {
		PersistentJobConfiguration cnf=
			database.getEntityManager().find(PersistentJobConfiguration.class,id.toString());
		return cnf!=null && this.database.deleteObject(cnf);
	}
	
}
