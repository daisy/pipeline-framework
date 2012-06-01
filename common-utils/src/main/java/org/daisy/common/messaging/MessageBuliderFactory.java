package org.daisy.common.messaging;

import org.daisy.common.messaging.Message.MessageBuilder;
import org.daisy.common.messaging.impl.DefaultMessage;

public class MessageBuliderFactory {

	public MessageBuilder newMessageBuilder(){
			return new DefaultMessage.DefaultMessageBuilder();
	}


}
