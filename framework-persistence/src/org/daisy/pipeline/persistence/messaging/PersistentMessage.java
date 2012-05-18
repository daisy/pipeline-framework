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
import org.daisy.pipeline.persistence.DaisyEntityManagerFactory;

@Entity
//@NoSql(dataFormat=DataFormatType.MAPPED)
public class PersistentMessage implements Message{
	@Column(name="throwable")
	
	 Throwable throwable;
	
	@Column(name="text")
	
	String text;

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
	
	@Column(name="line")
	private int line;
	
	@Column(name="col")
	private int column;
	
	@Column(name="file")
	private String file;
	
	
	public PersistentMessage(){};
	
	
	public PersistentMessage(Throwable throwable, String text, Level level,
			Date timeStamp, int sequence, String jobId, int line, int column,
			String file) {
		super();
		this.throwable = throwable;
		this.text = text;
		this.level = level;
		this.timeStamp = timeStamp;
		this.sequence = sequence;
		this.jobId = jobId;
		this.line = line;
		this.column = column;
		this.file = file;
	}
	
	public PersistentMessage(Message other){
		this.throwable = other.getThrowable();
		this.text = other.getText();
		this.level = other.getLevel();
		this.timeStamp = other.getTimeStamp();
		this.sequence = other.getSequence();
		this.jobId = other.getJobId();
		this.line = other.getLine();
		this.column = other.getColumn();
		this.file = other.getFile();
	}


	public Throwable getThrowable() {
		return throwable;
	}
	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}
	public String getText() {
		return text;
	}
	public void setText(String msg) {
		this.text = msg;
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
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}		
	public void setJobId(JobId jobId) {
		this.jobId = jobId.toString();
	}	


	@Override
	public int getLine() {
		return this.line;
	}
	public void setLine(int line) {
		this.line=line;
	}

	@Override
	public int getColumn() {
		
		return this.column;
	}
	public void setColumn(int column) {
		this.column=column;
	}

	@Override
	public String getFile() {
		return this.file;
	}
	public void setFile(String file) {
		this.file=file;
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
	
	


	

}
