package org.daisy.pipeline.logging;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.sift.MDCBasedDiscriminator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import org.daisy.common.messaging.Message;
import org.daisy.common.messaging.MessageBuliderFactory;
import org.daisy.pipeline.event.EventBusProvider;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;

/**
 * Configure like this:
 *
 * &lt;appender name="EVENTBUS" class="org.daisy.pipeline.logging.EventBusAppender"&gt;
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
public class EventBusAppender extends AppenderBase<ILoggingEvent> {
	
	private EventBusProvider eventBusProvider;
	private BundleContext bundleContext;
	private MessageBuliderFactory messageBuilderFactory;
	private List<ILoggingEvent> eventBuffer;
	private MDCBasedDiscriminator discriminator;
	
	@Override
	public void start() {
		bundleContext = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
		messageBuilderFactory = new MessageBuliderFactory();
		eventBuffer = new ArrayList<ILoggingEvent>();
		discriminator = new MDCBasedDiscriminator();
		discriminator.setKey("jobid");
		discriminator.setDefaultValue("default");
		super.start();
	}
	
	protected void append(ILoggingEvent event) {
		if (!isStarted())
			return;
		String jobId = discriminator.getDiscriminatingValue(event);
		if (!"default".equals(jobId)) {
			if (eventBusProvider == null) {
				try {
					eventBusProvider = bundleContext.getService(
						bundleContext.getServiceReferences(EventBusProvider.class, null).iterator().next());
					for (ILoggingEvent e : eventBuffer)
						append(eventBusProvider, discriminator.getDiscriminatingValue(e), e);
					eventBuffer.clear(); }
				catch (Exception e) {
					eventBuffer.add(event);
					return; }}
			append(eventBusProvider, jobId, event); }
	}
	
	private void append(EventBusProvider eventBus, String jobId, ILoggingEvent event) {
		Level level = event.getLevel();
		Message m = messageBuilderFactory.newMessageBuilder()
			.withTimeStamp(new Date())
			.withJobId(jobId)
			.withLevel(messageLevelFromLogbackLevel(level))
			.withText(event.getFormattedMessage())
			.build();
		eventBus.get().post(m);
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
