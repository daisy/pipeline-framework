package org.daisy.pipeline.persistence.messaging;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.daisy.common.messaging.Message;

@Entity
@IdClass(PersistenceMessagePK.class)
//@NoSql(dataFormat=DataFormatType.MAPPED)
public class PersistentMessage implements Message{
	private static final int TEXT_LEN=1024;
	private static final int FILE_LEN=512;

	@Column(name="throwable")
	private Throwable throwable;
	
	@Column(name="text",length=TEXT_LEN)
	private String text;

	/** The m level. */
	@Enumerated(EnumType.STRING)
//	@Column(name="level")
	private Level level;
	
	@Column(name="timestamp")
	@Temporal(TemporalType.TIMESTAMP)
	private Date timeStamp;
	
	@Id
	@Column(name="sequence")
	private int sequence;
	
	@Id
	@Column(name="jobId")
	private String jobId;
	
	@Column(name="line")
	private int line;
	
	@Column(name="col")
	private int column;
	
	@Column(name="file",length=FILE_LEN)
	private String file;
	
	
	public PersistentMessage(){};
	
	public PersistentMessage(Message other){
		this.throwable = other.getThrowable();
		this.text = trim(other.getText(),TEXT_LEN-1);
		this.level = other.getLevel();
		this.timeStamp = other.getTimeStamp();
		this.sequence = other.getSequence();
		this.jobId = other.getJobId();
		this.line = other.getLine();
		this.column = other.getColumn();
		this.file =trim( other.getFile(),FILE_LEN-1);
	}
	private static String trim(String str,int len){
		if(str!=null && str.length()>len){
			str=str.substring(0,len);
		}
		return str;
	}

	@Override
	public Throwable getThrowable() {
		return throwable;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public Message.Level getLevel() {
		return Message.Level.valueOf(level.toString());
	}

	@Override
	public Date getTimeStamp() {
		return timeStamp;
	}

	@Override
	public int getSequence() {
		return sequence;
	}

	@Override
	public String getJobId() {
		return jobId;
	}


	@Override
	public int getLine() {
		return this.line;
	}

	@Override
	public int getColumn() {
		
		return this.column;
	}

	@Override
	public String getFile() {
		return this.file;
	}
	
}
