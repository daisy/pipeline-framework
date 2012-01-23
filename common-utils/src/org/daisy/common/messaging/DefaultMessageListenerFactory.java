package org.daisy.common.messaging;


// TODO: Auto-generated Javadoc
/**
 * A factory for creating DefaultMessageListener objects.
 */
public class DefaultMessageListenerFactory implements MessageListenerFactory {

	/* (non-Javadoc)
	 * @see org.daisy.common.messaging.MessageListenerFactory#createMessageListener()
	 */
	@Override
	public  MessageListener createMessageListener(){
		return new MemoryMessageListener();
	}
}
