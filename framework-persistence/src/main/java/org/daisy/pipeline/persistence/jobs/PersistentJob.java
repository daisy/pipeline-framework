package org.daisy.pipeline.persistence.jobs;

import java.io.Serializable;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.daisy.pipeline.job.AbstractJobContext;
import org.daisy.pipeline.job.Job;

import org.daisy.pipeline.persistence.Database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name="jobs")
public class PersistentJob  extends Job implements Serializable {
	private static final Logger logger = LoggerFactory.getLogger(PersistentJob.class);
	public static final long serialVersionUID=1L;

	/* A job is just an executable context + status 
	 * Due to the limitations of jpa we cant persist superclass attributes
	 * unless the superclass is annotated.
	 * That's a not go as framework-core would depened on jpa.
	 *
	 * We'll store the status in the attribute currentStatus and use the 
	 * callbacks to update it
	 */
	@Enumerated(EnumType.ORDINAL)
	Status currentStatus;	
	@Id
	@Column(name="job_id")
	String sJobId;

	@OneToOne(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
	@MapsId("job_id")
	PersistentJobContext pCtxt;
	//the status changed will be watched by changeStatus
	//and this very object is in charge of updating itself
	@Transient
	Database db=null;
	public PersistentJob(Job job,Database db) {
		super(new PersistentJobContext((AbstractJobContext)job.getContext()),job.getStatus());
		this.db=db;
	}

	public PersistentJob(PersistentJob job,Database db) {
		super(job.getContext(),job.getStatus());
		this.db=db;
	}

	/**
	 * Constructs a new instance.
	 */
	public PersistentJob() {
		super(null);
	}



	@PrePersist
	@PreUpdate
	public void preCallback(){
			//this.currentStatus=this.status;
			this.pCtxt=(PersistentJobContext)this.ctxt;
			this.sJobId=this.getContext().getId().toString();
		
	}
	@PostLoad
	public void postCallback(){
		this.ctxt=this.pCtxt;
		this.setStatus(this.currentStatus);
	}

	public static List<Job> getAllJobs(Database db){
		List<Job> jobs=db.runQuery("select j from PersistentJob j",Job.class);
		return jobs;
		

	}
	//this will watch for changes in the status and update the db
	@Override
	protected synchronized void onStatusChanged(Job.Status to) {
		this.currentStatus=to;
		logger.info("Changing Status:"+to);	
		if(this.db!=null){
			logger.debug("Updating object");	
			db.updateObject(this);
		}
		
		
	}

	protected void setDatabase(Database db){
		this.db=db;
	}


}
