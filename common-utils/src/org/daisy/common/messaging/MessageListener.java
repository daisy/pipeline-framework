package org.daisy.common.messaging;


// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving message events.
 * The class that is interested in processing a message
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addMessageListener<code> method. When
 * the message event occurs, that object's appropriate
 * method is invoked.
 *
 * @see MessageEvent
 */
public interface MessageListener {
	
	/**
	 * Posts a trace.
	 *
	 * @param msg the msg
	 */
	public void trace(String msg);
	
	/**
	 * Posts a trace.
	 *
	 * @param msg the msg
	 * @param throwable the throwable
	 */
	public void trace(String msg,Throwable throwable);
	
	/**
	 * Posts a debug.
	 *
	 * @param msg the msg
	 */
	public void debug(String msg);
	
	/**
	 * Posts a debug.
	 *
	 * @param msg the msg
	 * @param throwable the throwable
	 */
	public void debug(String msg,Throwable throwable);
	
	/**
	 * Posts a info.
	 *
	 * @param msg the msg
	 */
	public void info(String msg);
	
	/**
	 * Posts a info.
	 *
	 * @param msg the msg
	 * @param throwable the throwable
	 */
	public void info(String msg,Throwable throwable);
	
	/**
	 * Posts a warn.
	 *
	 * @param msg the msg
	 */
	public void warn(String msg);
	
	/**
	 * Posts a warn.
	 *
	 * @param msg the msg
	 * @param throwable the throwable
	 */
	public void warn(String msg,Throwable throwable);
	
	/**
	 * Posts an error.
	 *
	 * @param msg the msg
	 */
	public void error(String msg);
	
	/**
	 * Posts an error.
	 *
	 * @param msg the msg
	 * @param throwable the throwable
	 */
	public void error(String msg,Throwable throwable);
	
	/**
	 * Gets the accessor.
	 *
	 * @return the accessor
	 */
	public MessageAccessor getAccessor();
}
