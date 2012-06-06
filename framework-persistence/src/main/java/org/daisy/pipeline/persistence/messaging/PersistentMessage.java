package org.daisy.pipeline.persistence.messaging;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.daisy.common.messaging.Message;
import org.daisy.pipeline.job.JobId;

@Entity
@IdClass(PersistenceMessagePK.class)
//@NoSql(dataFormat=DataFormatType.MAPPED)
public class PersistentMessage implements Message{
	public enum PersistentLevel {
		/** The ERROR. */
		ERROR,
		/** The WARNING. */
		WARNING,
		/** The INFO. */
		INFO,
		/** The DEBUG. */
		DEBUG,
		/** The TRACE. */
		TRACE;
		public static PersistentLevel fromLevel(Level level){
			return valueOf(level.name());
		}
		public static List<PersistentLevel> fromLevel(List<Level> levels) {
			List<PersistentLevel> res = new ArrayList<PersistentMessage.PersistentLevel>(levels.size());
			for (Level level : levels) {
				res.add(fromLevel(level));
			}
			return res;
		}
	}
	@Column(name="throwable")
	
	 Throwable throwable;
	
	@Column(name="text")
	
	String text;

	/** The m level. */
	@Enumerated(EnumType.STRING)
//	@Column(name="level")
	PersistentLevel level;
	
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
	
	public PersistentMessage(Message other){
		this.throwable = other.getThrowable();
		this.text = other.getText();
		this.level = PersistentLevel.valueOf(other.getLevel().toString());
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
	public Message.Level getLevel() {
		return Message.Level.valueOf(level.toString());
	}
	public void setLevel(PersistentLevel level) {
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
	
}
