package org.daisy.pipeline.event;

import java.util.function.BiConsumer;

import org.daisy.common.messaging.AbstractMessageAccessor;
import org.daisy.common.messaging.Message;
import org.daisy.common.messaging.MessageAccessor;
import org.daisy.pipeline.properties.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageAccessorFromStorage extends AbstractMessageAccessor {

	private static Logger logger = LoggerFactory.getLogger(LiveMessageAccessor.class);
	private static Message.Level threshold;
	static {
		try {
			threshold = Message.Level.valueOf(Properties.getProperty("org.daisy.pipeline.log.level", "INFO"));
		} catch (IllegalArgumentException e) {
			threshold = Message.Level.INFO;
		}
	}

	private final String id;
	private final MessageStorage storage;

	public MessageAccessorFromStorage(String id, MessageStorage storage) {
		super(threshold);
		this.id = id;
		this.storage = storage;
		logger.trace("Created MessageAccessorFromStorage for job " + id + " and message storage " + storage);
	}

	@Override
	protected Iterable<Message> allMessages() {
		return storage.get(id);
	}

	// It is assumed that messages are immutable once stored and that no new messages are produced.
	@Override
	public void listen(BiConsumer<MessageAccessor,Integer> callback) {}
	@Override
	public void unlisten(BiConsumer<MessageAccessor,Integer> callback) {}

}
