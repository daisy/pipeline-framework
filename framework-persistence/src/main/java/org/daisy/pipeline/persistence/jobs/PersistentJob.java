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
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.AbstractJobContext;
import org.daisy.pipeline.job.JobContext;

import org.daisy.pipeline.persistence.Database;

@Entity
@Table(name="jobs")
public class PersistentJob  extends Job implements Serializable {

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
		super(new PersistentJobContext(job.getContext()));
		this.status=job.getStatus();
		this.db=db;
	}
	public PersistentJob(PersistentJob job,Database db) {
		super(job.getContext());
		this.status=job.getStatus();
		this.db=db;
	}

	/**
	 * Constructs a new instance.
	 */
	public PersistentJob() {
		super(null);
	}

	public void setContext(JobContext ctxt){
		this.ctxt=new PersistentJobContext(ctxt);
	}
	public void setContext(PersistentJobContext ctxt){
		this.ctxt=ctxt;
	}


	/**
	 * Gets the currentStatus for this instance.
	 *
	 * @return The currentStatus.
	 */
	public Status getCurrentStatus() {
		return this.currentStatus;
	}

	/**
	 * Sets the currentStatus for this instance.
	 *
	 * @param currentStatus The currentStatus.
	 */
	public void setCurrentStatus(Status currentStatus) {
		this.currentStatus = currentStatus;
	}

	@PrePersist
	@PreRemove
	@PreUpdate
	public void preCallback(){
			//this.currentStatus=this.status;
			this.pCtxt=(PersistentJobContext)this.ctxt;
			this.sJobId=this.getContext().getId().toString();
		
	}
	@PostLoad
	public void postCallback(){
		this.ctxt=this.pCtxt;
		this.status=this.currentStatus;
	}

	//public static Iterable<JobId> getAllJobIds(Database db){
		//List<String> ids=db.runQuery("select distinct j.job_id from PersistentJob j",String.class);
		//return Collections2.transform(ids,new Function<String,JobId>(){
			//@Override
			//public JobId apply(String sId) {
				//return JobIdFactory.newIdFromString(sId);
			//}
		//});

	//}
	public static List<Job> getAllJobs(Database db){
		List<Job> jobs=db.runQuery("select j from PersistentJob j",Job.class);
		return jobs;
		

	}
	//this will watch for changes in the status and update the db
	@Override
	protected void changeStatus(Job.Status to) {
		Job.Status oldStatus = this.status;
		super.changeStatus(to);
		this.setCurrentStatus(to);
		if(this.db!=null)
			db.updateObject(this);
		
	}

	protected void setDatabase(Database db){
		this.db=db;
	}

}
