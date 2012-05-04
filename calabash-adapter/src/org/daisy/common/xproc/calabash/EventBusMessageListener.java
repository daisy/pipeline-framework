package org.daisy.common.xproc.calabash;

import java.util.Date;
import java.util.Properties;

import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.XdmNode;

import org.daisy.pipeline.event.EventBusProvider;
import org.daisy.pipeline.messaging.Message.Level;
import org.daisy.pipeline.messaging.Message.MessageBuilder;
import org.daisy.pipeline.messaging.MessageBuliderFactory;

import com.xmlcalabash.core.XProcMessageListener;
import com.xmlcalabash.core.XProcRunnable;

// TODO: Auto-generated Javadoc
/**
 * Wrapps the org.daisy.common.messaging.MessageListener to a
 * XProcMessageListener to be plugged in calabash
 */
public class EventBusMessageListener implements XProcMessageListener {

	/** The listener. */
	EventBusProvider eventBus;
	MessageBuliderFactory messageBuilderFactory;
	Properties props;
	int sequence = 0;

	public EventBusMessageListener(EventBusProvider eventBus,
			MessageBuliderFactory messageBuilderFactory, Properties props) {
		super();
		this.eventBus = eventBus;
		this.messageBuilderFactory = messageBuilderFactory;
		this.props = props;
	}

	private void post(MessageBuilder builder) {
		if (props.contains("JOB_ID")) {
			builder.withJobId(props.getProperty("JOB_ID"));
		}
		builder.withSequence(sequence++);
		builder.withTimeStamp(new Date());
		eventBus.get().post(builder.build());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.xmlcalabash.core.XProcMessageListener#error(java.lang.Throwable)
	 */
	@Override
	public void error(Throwable exception) {
		MessageBuilder builder = messageBuilderFactory.newMessageBuilder()
				.withLevel(Level.ERROR);
		XprocMessageHelper.errorMessage(exception, builder);
		post(builder);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.xmlcalabash.core.XProcMessageListener#error(com.xmlcalabash.core.
	 * XProcRunnable, net.sf.saxon.s9api.XdmNode, java.lang.String,
	 * net.sf.saxon.s9api.QName)
	 */
	@Override
	public void error(XProcRunnable step, XdmNode node, String message,
			QName qName) {
		MessageBuilder builder = messageBuilderFactory.newMessageBuilder()
				.withLevel(Level.ERROR);
		builder = XprocMessageHelper.message(step, node, message, builder);
		post(builder);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.xmlcalabash.core.XProcMessageListener#fine(com.xmlcalabash.core.
	 * XProcRunnable, net.sf.saxon.s9api.XdmNode, java.lang.String)
	 */
	@Override
	public void fine(XProcRunnable step, XdmNode node, String message) {
		MessageBuilder builder = messageBuilderFactory.newMessageBuilder()
				.withLevel(Level.DEBUG);
		builder = XprocMessageHelper.message(step, node, message, builder);
		post(builder);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.xmlcalabash.core.XProcMessageListener#finer(com.xmlcalabash.core.
	 * XProcRunnable, net.sf.saxon.s9api.XdmNode, java.lang.String)
	 */
	@Override
	public void finer(XProcRunnable step, XdmNode node, String message) {
		MessageBuilder builder = messageBuilderFactory.newMessageBuilder()
				.withLevel(Level.DEBUG);
		builder = XprocMessageHelper.message(step, node, message, builder);
		post(builder);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.xmlcalabash.core.XProcMessageListener#finest(com.xmlcalabash.core
	 * .XProcRunnable, net.sf.saxon.s9api.XdmNode, java.lang.String)
	 */
	@Override
	public void finest(XProcRunnable step, XdmNode node, String message) {
		MessageBuilder builder = messageBuilderFactory.newMessageBuilder()
				.withLevel(Level.DEBUG);
		builder = XprocMessageHelper.message(step, node, message, builder);
		post(builder);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.xmlcalabash.core.XProcMessageListener#info(com.xmlcalabash.core.
	 * XProcRunnable, net.sf.saxon.s9api.XdmNode, java.lang.String)
	 */
	@Override
	public void info(XProcRunnable step, XdmNode node, String message) {
		MessageBuilder builder = messageBuilderFactory.newMessageBuilder()
				.withLevel(Level.INFO);
		builder = XprocMessageHelper.message(step, node, message, builder);
		post(builder);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.xmlcalabash.core.XProcMessageListener#warning(com.xmlcalabash.core
	 * .XProcRunnable, net.sf.saxon.s9api.XdmNode, java.lang.String)
	 */
	@Override
	public void warning(XProcRunnable step, XdmNode node, String message) {
		MessageBuilder builder = messageBuilderFactory.newMessageBuilder()
				.withLevel(Level.WARNING);
		builder = XprocMessageHelper.message(step, node, message, builder);
		post(builder);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.xmlcalabash.core.XProcMessageListener#warning(java.lang.Throwable)
	 */
	@Override
	public void warning(Throwable exception) {
		MessageBuilder builder = messageBuilderFactory.newMessageBuilder()
				.withLevel(Level.WARNING);
		XprocMessageHelper.errorMessage(exception, builder);
		post(builder);

	}

}
