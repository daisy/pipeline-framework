package org.daisy.common.messaging;


public class DefaultMessageListenerFactory implements MessageListenerFactory {
	public  MessageListener createMessageListener(){
		return new MemoryMessageListener();
	}
}
