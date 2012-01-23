package org.daisy.common.messaging;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating MessageListener objects.
 */
public interface MessageListenerFactory {

	/**
	 * Creates a new MessageListener object.
	 *
	 * @return the message listener
	 */
	public  MessageListener createMessageListener();
}