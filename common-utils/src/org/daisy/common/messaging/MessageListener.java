package org.daisy.common.messaging;


public interface MessageListener {
	public void trace(String msg);
	public void trace(String msg,Throwable throwable);
	public void debug(String msg);
	public void debug(String msg,Throwable throwable);
	public void info(String msg);
	public void info(String msg,Throwable throwable);
	public void warn(String msg);
	public void warn(String msg,Throwable throwable);
	public void error(String msg);
	public void error(String msg,Throwable throwable);
	public MessageAccessor getAccessor();
}
