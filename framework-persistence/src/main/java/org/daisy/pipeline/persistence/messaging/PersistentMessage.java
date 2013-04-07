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

@Entity
@IdClass(PersistenceMessagePK.class)
//@NoSql(dataFormat=DataFormatType.MAPPED)
public class PersistentMessage extends Message{
	private static final int TEXT_LEN=1024;
	private static final int FILE_LEN=512;
	
	public PersistentMessage(){
		super(null,null,null,0,0,null,0,null,null);
	};
	
	public PersistentMessage(Message other){
		this();
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

	/**
	 * @return the throwable
	 */
	@Column(name="throwable")
	@Override
	public Throwable getThrowable() {
		return this.throwable;
	}
	/**
	 * @param throwable the throwable to set
	 */
	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}

	/**
	 * @return the text
	 */
	@Column(name="text",length=TEXT_LEN)
	@Override
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the level
	 */
	@Enumerated(EnumType.STRING)
	@Override
	public Message.Level getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(Message.Level level) {
		this.level = level;
	}

	/**
	 * @return the timeStamp
	 */
	@Column(name="timestamp")
	@Temporal(TemporalType.TIMESTAMP)
	@Override
	public Date getTimeStamp() {
		return timeStamp;
	}

	/**
	 * @param timeStamp the timeStamp to set
	 */
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	/**
	 * @return the sequence
	 */
	@Id
	@Column(name="sequence")
	@Override
	public int getSequence() {
		return sequence;
	}

	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	/**
	 * @return the jobId
	 */
	@Id
	@Column(name="jobId")
	@Override
	public String getJobId() {
		return jobId;
	}
	/**
	 * @param jobId the jobId to set
	 */
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	/**
	 * @return the line
	 */
	@Column(name="line")
	@Override
	public int getLine() {
		return line;
	}

	/**
	 * @param line the line to set
	 */
	public void setLine(int line) {
		this.line = line;
	}

	/**
	 * @return the column
	 */
	@Column(name="col")
	@Override
	public int getColumn() {
		return column;
	}

	/**
	 * @param column the column to set
	 */
	public void setColumn(int column) {
		this.column = column;
	}

	/**
	 * @return the file
	 */
	@Column(name="file",length=FILE_LEN)
	@Override
	public String getFile() {
		return file;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(String file) {
		this.file = file;
	}
	
}
