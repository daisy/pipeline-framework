package org.daisy.common.messaging;

import java.util.Date;

public class Message {
	public enum Level{ERROR,WARNING,INFO,DEBUG,TRACE}
	final Throwable mThrowable;
	final String mMsg;
	final Level mLevel;
	final Date mTimeStamp;  

	private Message(Level level, String msg, Throwable throwable) {
		mLevel=level;
		mMsg=msg;
		mThrowable=throwable;
		mTimeStamp= new Date();
	}

	public Throwable getThrowable() {
		return mThrowable;
	}


	public String getMsg() {
		return mMsg;
	}


	public Level getLevel() {
		return mLevel;
	}
	
	public Date getTimeStamp() {
		return mTimeStamp;
	}

	public static final class Builder{
		Throwable mThrowable;
		String mMsg;
		Level mLevel;
		public Builder withMessage(String message){
			mMsg=message;
			return this;
		}
		public Builder withLevel(Level level){
			mLevel=level;
			return this;
		}
		public Builder withThrowable(Throwable throwable){
			mThrowable=throwable;
			return this;
		}
		
		public Message build(){
			return new Message(mLevel,mMsg,mThrowable);
		}
	}
}
