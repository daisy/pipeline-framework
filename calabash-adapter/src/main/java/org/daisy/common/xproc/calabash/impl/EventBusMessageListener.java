package org.daisy.common.xproc.calabash.impl;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Deque;

import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.XdmNode;

import org.daisy.common.messaging.Message.Level;
import org.daisy.common.messaging.MessageAppender;
import org.daisy.common.messaging.MessageBuilder;
import org.daisy.pipeline.event.EventBusProvider;

import com.xmlcalabash.core.XProcMessageListener;
import com.xmlcalabash.core.XProcRunnable;
import com.xmlcalabash.runtime.XStep;

/**
 * Wrapps the org.daisy.common.messaging.MessageListener to a
 * XProcMessageListener to be plugged in calabash
 */
public class EventBusMessageListener implements XProcMessageListener {

	private final String jobId;
	private final EventBusProvider eventBus;
	private final boolean autoNameSteps;
	private final Deque<MessageAppender> messageStack = new ArrayDeque<>();

	/**
	 * @param autoNameSteps Set this property to automatically add a message to all steps with
	 *        progress information but no message (only for debugging)
	 */
	public EventBusMessageListener(EventBusProvider eventBus, String jobId, boolean autoNameSteps) {
		super();
		this.eventBus = eventBus;
		this.jobId = jobId;
		this.autoNameSteps = autoNameSteps;
	}

	private MessageAppender post(MessageBuilder builder, boolean close) {
		MessageAppender current = messageStack.isEmpty() ? eventBus : messageStack.peek();
		if (messageStack.isEmpty())
			builder = builder.withOwnerId(jobId);
		current = current.append(builder);
		if (!close)
			messageStack.push(current);
		else
			current.close();
		return current;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.xmlcalabash.core.XProcMessageListener#error(java.lang.Throwable)
	 */
	@Override
	public void error(Throwable exception) {
		MessageBuilder builder = new MessageBuilder()
				.withLevel(Level.ERROR);
		XprocMessageHelper.errorMessage(exception, builder);
		post(builder, true);
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
		MessageBuilder builder = new MessageBuilder()
				.withLevel(Level.ERROR);
		builder = XprocMessageHelper.message(step, node, message, builder);
		post(builder, true);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.xmlcalabash.core.XProcMessageListener#fine(com.xmlcalabash.core.
	 * XProcRunnable, net.sf.saxon.s9api.XdmNode, java.lang.String)
	 */
	@Override
	public void fine(XProcRunnable step, XdmNode node, String message) {
		MessageBuilder builder = new MessageBuilder()
				.withLevel(Level.DEBUG);
		builder = XprocMessageHelper.message(step, node, message, builder);
		post(builder, true);
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
		MessageBuilder builder = new MessageBuilder()
				.withLevel(Level.TRACE);
		builder = XprocMessageHelper.message(step, node, message, builder);
		post(builder, true);
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
		MessageBuilder builder = new MessageBuilder()
				.withLevel(Level.TRACE);
		builder = XprocMessageHelper.message(step, node, message, builder);
		post(builder, true);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.xmlcalabash.core.XProcMessageListener#info(com.xmlcalabash.core.
	 * XProcRunnable, net.sf.saxon.s9api.XdmNode, java.lang.String)
	 */
	@Override
	public void info(XProcRunnable step, XdmNode node, String message) {
		MessageBuilder builder = new MessageBuilder()
				.withLevel(Level.INFO);
		builder = XprocMessageHelper.message(step, node, message, builder);
		post(builder, true);
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
		MessageBuilder builder = new MessageBuilder()
				.withLevel(Level.WARNING);
		builder = XprocMessageHelper.message(step, node, message, builder);
		post(builder, true);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.xmlcalabash.core.XProcMessageListener#warning(java.lang.Throwable)
	 */
	@Override
	public void warning(Throwable exception) {
		MessageBuilder builder = new MessageBuilder()
				.withLevel(Level.WARNING);
		XprocMessageHelper.errorMessage(exception, builder);
		post(builder, true);
	}

	@Override
	public void openStep(XProcRunnable step, XdmNode node, String message, String level, BigDecimal portion) {
		MessageBuilder builder = new MessageBuilder().withProgress(portion);
		if (message == null && autoNameSteps && portion != null && portion.compareTo(BigDecimal.ZERO) > 0) {
			// FIXME: not if there is a an ancestor block with portion 0!
			// how to test this?
			// -> activeBlock.getPortion() returns portion as defined in XPL and no access to parents
			// -> only check parent and change portion to 0 if parent has portion 0 because irrelevant anyway
			if (step instanceof XStep)
				message = ((XStep)step).getStep().getName();
			if (level == null)
				level = "DEBUG";
		}
		if (level == null || level.equals("INFO")) {
			builder.withLevel(Level.INFO);
		} else if (level.equals("ERROR")) {
			builder.withLevel(Level.ERROR);
		} else if (level.equals("WARN")) {
			builder.withLevel(Level.WARNING);
		} else if (level.equals("DEBUG")) {
			builder.withLevel(Level.DEBUG);
		} else if (level.equals("TRACE")) {
			builder.withLevel(Level.TRACE);
		} else {
			builder.withLevel(Level.INFO);
			message = "Message with invalid level '" + level + "': " + message;
		}
		XprocMessageHelper.message(step, node, message, builder);
		post(builder, false);
	}

	@Override
	public void closeStep() {
		if (messageStack.isEmpty())
			throw new RuntimeException("coding error");
		MessageAppender current = messageStack.pop();
		current.close();
	}
}
