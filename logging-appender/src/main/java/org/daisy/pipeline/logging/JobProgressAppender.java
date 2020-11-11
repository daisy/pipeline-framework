package org.daisy.pipeline.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.sift.MDCBasedDiscriminator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import org.daisy.common.messaging.Message;
import org.daisy.common.messaging.MessageAppender;
import org.daisy.common.messaging.MessageBuilder;

/**
 * Append to the current job progress thread.
 *
 * Configure like this:
 *
 * &lt;appender name="JOB" class="org.daisy.pipeline.logging.JobProgressAppender"&gt;
 *   &lt;filter class="org.daisy.pipeline.logging.ThresholdFilter"&gt;
 *     &lt;rootLevel&gt;INFO&lt;/rootLevel&gt;
 *     &lt;loggerLevels&gt;
 *       cz.vutbr.web=WARN
 *       org.daisy.common.xproc.calabash.steps.Message=WARN
 *       com.xmlcalabash.runtime.XAtomicStep=WARN
 *     &lt;/loggerLevels&gt;
 *   &lt;/filter&gt;
 * &lt;/appender&gt;
 */
public class JobProgressAppender extends AppenderBase<ILoggingEvent> {

	// MessageAppender.getActiveBlockLogger() can not be used here because the logback appender is
	// run in a separate thread. Using MDCBasedDiscriminator instead.
	private MDCBasedDiscriminator threadDiscriminator;

	@Override
	public void start() {
		threadDiscriminator = new MDCBasedDiscriminator();
		threadDiscriminator.setKey("message-thread");
		threadDiscriminator.setDefaultValue("default");
		super.start();
	}

	protected void append(ILoggingEvent event) {
		if (!isStarted())
			return;
		String threadId = threadDiscriminator.getDiscriminatingValue(event);
		if (!"default".equals(threadId)) {
			MessageAppender activeBlock = MessageAppender.getActiveBlock(threadId);
			// we need an active block otherwise we have no place to send the message to
			if (activeBlock != null) {
				Level level = event.getLevel();
				activeBlock.append(
					new MessageBuilder()
					    .withLevel(messageLevelFromLogbackLevel(level))
					    .withText(event.getFormattedMessage())
				).close();
			}
		}
	}

	private static Message.Level messageLevelFromLogbackLevel(Level level) {
		switch(level.toInt()) {
		case Level.TRACE_INT:
			return Message.Level.TRACE;
		case Level.DEBUG_INT:
			return Message.Level.DEBUG;
		case Level.INFO_INT:
			return Message.Level.INFO;
		case Level.WARN_INT:
			return Message.Level.WARNING;
		case Level.ERROR_INT:
			return Message.Level.ERROR;
		case Level.ALL_INT:
		case Level.OFF_INT:
		default:
			return null; }
	}
}
