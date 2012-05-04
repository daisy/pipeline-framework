package org.daisy.pipeline.messaging;

import org.daisy.pipeline.messaging.Message.MessageBuilder;
import org.daisy.pipeline.messaging.impl.DefaultMessage;

public class MessageBuliderFactory {

	public MessageBuilder newMessageBuilder(){
			return new DefaultMessage.DefaultMessageBuilder();
	}


}
