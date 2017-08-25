package org.daisy.pipeline.event;

import java.util.Date;

import com.google.common.base.Supplier;
import com.google.common.eventbus.EventBus;

import org.daisy.common.messaging.Message;
import org.daisy.common.slf4j.AbstractLogger;

import org.slf4j.Logger;
import org.slf4j.MDC;

public class EventBusProvider implements Supplier<EventBus>{

	private final EventBus mEventBus=new EventBus();//AsyncEventBus(Executors.newFixedThreadPool(10));

	@Override
	public EventBus get() {
		return mEventBus;
	}

	/**
	 * SLF4J Logger that sends Message events with the job id of the current job to the
	 * EventBus.
	 */
	public Logger getAsLogger() {
		return asLogger;
	}

	private final Logger asLogger = new AbstractLogger() {
		
		public boolean isTraceEnabled() {
			return true;
		}

		public boolean isDebugEnabled() {
			return true;
		}

		public boolean isInfoEnabled() {
			return true;
		}

		public boolean isWarnEnabled() {
			return true;
		}

		public boolean isErrorEnabled() {
			return true;
		}

		protected void doTrace(String msg) {
			postMessage(msg, Message.Level.TRACE);
		}

		protected void doDebug(String msg) {
			postMessage(msg, Message.Level.DEBUG);
		}

		protected void doInfo(String msg) {
			postMessage(msg, Message.Level.INFO);
		}

		protected void doWarn(String msg) {
			postMessage(msg, Message.Level.WARNING);
		}

		protected void doError(String msg) {
			postMessage(msg, Message.Level.ERROR);
		}

		// depends on MDC manipulation of DefaultJobExecutionService
		private void postMessage(String msg, Message.Level level) {
			String jobId = MDC.get("jobid");
			if (jobId != null)
				mEventBus.post(new Message.MessageBuilder()
				                   .withJobId(jobId)
				                   .withTimeStamp(new Date())
				                   .withLevel(level)
				                   .withText(msg)
				                   .build());
		}
	};
}
