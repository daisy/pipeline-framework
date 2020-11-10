package org.daisy.pipeline.event;

import java.util.function.BiConsumer;
import java.util.LinkedList;
import java.util.List;

import org.daisy.common.messaging.AbstractMessageAccessor;
import org.daisy.common.messaging.Message;
import org.daisy.common.messaging.MessageAccessor;
import org.daisy.common.messaging.MessageUpdate;
import org.daisy.common.messaging.ProgressMessage;
import org.daisy.pipeline.properties.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class receives message events for a particular job, buffers them, makes them accessible
 * through a MessageFilte.
 */
public class LiveMessageAccessor extends AbstractMessageAccessor {

	private static Logger logger = LoggerFactory.getLogger(LiveMessageAccessor.class);
	private static Message.Level threshold;
	static {
		try {
			threshold = Message.Level.valueOf(Properties.getProperty("org.daisy.pipeline.log.level", "INFO"));
		} catch (IllegalArgumentException e) {
			threshold = Message.Level.INFO;
		}
	}

	final String id;
	private final MessageEventListener eventListener;
	private final List<BiConsumer<MessageAccessor,Integer>> callbacks;
	private final List<Message> messages;

	public LiveMessageAccessor(String id, MessageEventListener eventListener) {
		super(threshold);
		this.id = id;
		this.eventListener = eventListener;
		this.callbacks = new LinkedList<>();
		this.messages = new LinkedList<>();
		eventListener.listen(this);
		logger.trace("Created LiveMessageAccessor for job " + id);
	}

	@Override
	protected Iterable<Message> allMessages() {
		return messages;
	}

	@Override
	public void listen(BiConsumer<MessageAccessor,Integer> callback) {
		synchronized (callbacks) {
			callbacks.add(callback);
		}
	}

	@Override
	public void unlisten(BiConsumer<MessageAccessor,Integer> callback) {
		synchronized (callbacks) {
			callbacks.remove(callback);
		}
	}

	void handleMessage(ProgressMessage msg) {
		synchronized (messages) {
			messages.add(msg);
		}
		synchronized (callbacks) {
			for (BiConsumer<MessageAccessor,Integer> c : callbacks)
				c.accept(this, msg.getSequence());
		}
	}

	void handleMessageUpdate(MessageUpdate update) {
		synchronized (callbacks) {
			for (BiConsumer<MessageAccessor,Integer> c : callbacks)
				c.accept(this, update.getSequence());
		}
	}

	/**
	 * @param storage Store the buffered messages
	 */
	void store(MessageStorage storage) {
		logger.trace("Persisting messages to " + storage);
		synchronized (messages) {
			for (Message m : messages)
				storage.add(m);
		}
	}

	/** Stop listening to message updates. */
	void close() {
		eventListener.unlisten(this);
		synchronized (messages) {
			messages.clear();
		}
	}
}
