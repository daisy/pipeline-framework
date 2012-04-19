package org.daisy.pipeline.persistence.messaging;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.daisy.common.messaging.Message;
import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.job.JobIdFactory;
import org.daisy.pipeline.persistence.DaisyEntityManagerFactory;
import org.eclipse.persistence.annotations.DataFormatType;
import org.eclipse.persistence.annotations.NoSql;

@Entity
@NoSql(dataFormat=DataFormatType.MAPPED)
public class PersistentMessage implements Message{
	@Column(name="throwable")
	/** The m throwable. */
	 Throwable throwable;
	@Column(name="message")
	/** The m msg. */
	String msg;

	@Enumerated
	/** The m level. */
	Level level;
	
	@Column(name="timestamp")
	@Temporal(TemporalType.TIMESTAMP)
	Date timeStamp;
	@Id
	@Column(name="sequence")
	int sequence;
	@Id
	@Column(name="jobId")
	String jobId;
	
	
	public PersistentMessage(){};
	public PersistentMessage(Throwable throwable, String msg, Level level,
			 int sequence, JobId jobId) {
		super();
		this.throwable = throwable;
		this.msg = msg;
		this.level = level;
		this.timeStamp = new Date();
		this.sequence = sequence;
		this.jobId = jobId.toString();
	}
	public Throwable getThrowable() {
		return throwable;
	}
	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Level getLevel() {
		return level;
	}
	public void setLevel(Level level) {
		this.level = level;
	}
	public Date getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	public JobId getJobId() {
		return JobIdFactory.newIdFromString(jobId);
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}		
	public void setJobId(JobId jobId) {
		this.jobId = jobId.toString();
	}	
	
	public static List<Message> getMessages(JobId id,int from,List<Level> levels){
		EntityManager em = DaisyEntityManagerFactory.createEntityManager();
		/*
		StringBuilder sqlBuilder=new StringBuilder("select m from PersistentMessage m where m.jobId='%s' and  m.sequence > %s and m.level in ( ");
		
		for (int i=0;i<levels.size();i++){
			sqlBuilder.append(" ?"+(i+1) );
			if(i!=levels.size()-1)
				sqlBuilder.append(", ");
		}
		sqlBuilder.append(") order by m.sequence ");
		String sql=String.format(sqlBuilder.toString(), id.toString(),from);
		*/
		StringBuilder sqlBuilder=new StringBuilder("select m from PersistentMessage m where m.jobId='%s' and  m.sequence > %s");
		String sql=String.format(sqlBuilder.toString(), id.toString(),from);
		Query q=em.createQuery(sql);
		/*
		int i=1;
		for (Level l:levels){
			q.setParameter(i++, l);
		}
		*/
		@SuppressWarnings("unchecked") //just how persistence works
		List<Message> result = q.getResultList();
		em.close();
		return result;
	}
	
	/**
	 * Builder for creating new messages
	 */
	public static final class Builder {

		/** The m throwable. */
		Throwable mThrowable;

		/** The m msg. */
		String mMsg;

		/** The m level. */
		Level mLevel;

		int mSequence;

		JobId mJobId;
		/**
		 * With message.
		 *
		 * @param message
		 *            the message
		 * @return the builder
		 */
		public Builder withMessage(String message) {
			mMsg = message;
			return this;
		}

		/**
		 * With level.
		 *
		 * @param level
		 *            the level
		 * @return the builder
		 */
		public Builder withLevel(Level level) {
			mLevel = level;
			return this;
		}

		/**
		 * With throwable.
		 *
		 * @param throwable
		 *            the throwable
		 * @return the builder
		 */
		public Builder withThrowable(Throwable throwable) {
			mThrowable = throwable;
			return this;
		}
		public Builder withSequence(int i) {
			mSequence=i;
			return this;
		}
		public Builder withJobId(JobId id) {
			mJobId=id;
			return this;
		}
		/**
		 * Builds the message based on the objects provided using the "with" methods.
		 *
		 * @return the message
		 */
		public PersistentMessage build() {
			return new PersistentMessage(mThrowable, mMsg,mLevel,mSequence,mJobId);
		}

		
	}

	

}
