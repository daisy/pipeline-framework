package org.daisy.maven.xproc.pipeline;

import com.google.common.eventbus.Subscribe;

import org.daisy.common.messaging.Message;
import org.daisy.pipeline.event.EventBusProvider;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class MessageEventListener {
	
	@Reference(
		name = "EventBusProvider",
		unbind = "-",
		service = EventBusProvider.class,
		cardinality = ReferenceCardinality.MANDATORY,
		policy = ReferencePolicy.STATIC
	)
	public void setEventBusProvider(EventBusProvider provider) {
		provider.get().register(this);
	}
	
	@Subscribe
	public synchronized void handleMessage(Message message) {
		String m = message.getText();
		switch (message.getLevel()) {
		case TRACE:
			logger.trace(m);
			break;
		case DEBUG:
			logger.debug(m);
			break;
		case INFO:
			logger.info(m);
			break;
		case WARNING:
			logger.warn(m);
			break;
		case ERROR:
			logger.error(m);
			break; }
	}
	
	private static final Logger logger = LoggerFactory.getLogger(MessageEventListener.class);
	
}
