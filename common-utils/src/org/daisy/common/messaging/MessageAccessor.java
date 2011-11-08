package org.daisy.common.messaging;

import java.util.List;

import org.daisy.common.messaging.Message.Level;


/**
 * Gives access to the stored messages by level.
 */
public interface MessageAccessor{
  
	/**
	 * Gets the errors.
	 *
	 * @return the error messages
	 */
	public List<Message> getErrors();
	
	/**
	 * Gets the warnings.
	 *
	 * @return the warning messages
	 */
	public List<Message> getWarnings();
	
	/**
	 * Gets the infos.
	 *
	 * @return the info messages
	 */
	public List<Message> getInfos();
	
	/**
	 * Gets the debugs.
	 *
	 * @return the debug messages
	 */
	public List<Message> getDebugs();
	
	/**
	 * Gets the traces.
	 *
	 * @return the trace messages
	 */
	public List<Message> getTraces();
	
	/**
	 * Gets the messgages from a set of levels
	 *
	 * @param fromLevels levels
	 * @return the messages
	 */
	public List<Message> getMessages(Level... fromLevels);
}
