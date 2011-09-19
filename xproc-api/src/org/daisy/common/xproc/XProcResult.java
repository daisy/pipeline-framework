package org.daisy.common.xproc;
import org.daisy.common.messaging.MessageAccessor;



// TODO: Auto-generated Javadoc
/**
 * The Interface XProcResult.
 */
public interface XProcResult {
	
	/**
	 * Writes the output.
	 *
	 * @param output the output
	 */
	void writeTo(XProcOutput output);
	
	/**
	 * Gets the messages produced during the pipeline execution. 
	 *
	 * @return the messages
	 */
	MessageAccessor getMessages();
}
