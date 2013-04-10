package org.daisy.pipeline.persistence.jobs;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;

import org.daisy.pipeline.job.AbstractJobContext;
import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.JobContext;
import org.daisy.pipeline.persistence.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

@Entity
//This plus the correct configuration 
//of the pu should be enough to ensure that jobs 
//with the same id will return true to
//job1==job2
//namely, only one object wandering around per JobId
@Cacheable
@Table(name="jobs")
@NamedQuery ( name="Job.getAll", query="select j from PersistentJob j")
@Access(value=AccessType.FIELD)
public class PersistentJob  extends Job implements Serializable {


	public static class PersistentJobBuilder extends JobBuilder{
		private Database db;

		/**
		 * @param db
		 */
		public PersistentJobBuilder(Database db) {
			this.db = db;
		}

		@Override
		protected Job initJob(){
			Job pjob=new PersistentJob(this.ctxt,this.bus,this.db);
			this.db.addObject(pjob);	
			return pjob;
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(PersistentJob.class);
	public static final long serialVersionUID=1L;

	/* A job is just an executable context + status 
	 * Due to the limitations of jpa we cant persist superclass attributes
	 * unless the superclass is annotated. 
	 * So here we follow a bean approach
	 * The id is proxified.
	 */
	@Id
	@Column(name="job_id")
	String sJobId;

	//the status changed will be watched by changeStatus
	//and this very object is in charge of updating itself
	@Transient
	Database db=null;


	private PersistentJob(JobContext ctxt,EventBus bus,Database db) {
		super(new PersistentJobContext((AbstractJobContext)ctxt),bus);
		this.db=db;
		this.sJobId=ctxt.getId().toString();
	}


	/**
	 * Constructs a new instance.
	 */
	private PersistentJob() {
		super(null,null);
	}

	/**
	 * @return the currentStatus
	 */
	@Enumerated(EnumType.ORDINAL)
	@Access(value=AccessType.PROPERTY)
	@Override
	public Status getStatus() {
		return super.getStatus();
	}

	/**
	 * @param currentStatus the currentStatus to set
	 */
	@Override
	public void setStatus(Status currentStatus) {
		super.setStatus(currentStatus);
	}

	/**
	 * @return the pCtxt
	 */
	@OneToOne(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
	@MapsId("job_id")
	@Access(value=AccessType.PROPERTY)
	public PersistentJobContext getContext() {
		return (PersistentJobContext)super.getContext();
	}

	/**
	 * @param pCtxt the pCtxt to set
	 */
	public void setContext(PersistentJobContext pCtxt) {
		super.ctxt= pCtxt;
	}


	public static List<Job> getAllJobs(Database db){
		TypedQuery<Job> query =
			      db.getEntityManager().createNamedQuery("Job.getAll", Job.class);
		List<Job> jobs=query.getResultList();
		return jobs;
		

	}

	//this will watch for changes in the status and update the db
	@Override
	protected synchronized void onStatusChanged(Job.Status to) {
		logger.info("Changing Status:"+to);	
		if(this.db!=null){
			logger.debug("Updating object");	
			db.updateObject(this);
		}else{
			logger.warn("Object not updated as the Database is null");
		}
	}

	protected void setDatabase(Database db){
		this.db=db;
	}

}
