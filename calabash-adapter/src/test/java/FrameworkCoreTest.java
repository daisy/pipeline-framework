import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.AppenderBase;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.collect.Iterators;
import com.google.common.io.CharStreams;

import org.daisy.common.messaging.Message;
import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcOutput;
import org.daisy.pipeline.clients.Client;
import org.daisy.pipeline.event.EventBusProvider;
import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.JobManager;
import org.daisy.pipeline.job.JobManagerFactory;

import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.junit.AbstractTest;
import org.daisy.pipeline.script.BoundXProcScript;
import org.daisy.pipeline.script.ScriptRegistry;
import org.daisy.pipeline.script.XProcScriptService;
import org.daisy.pipeline.webserviceutils.storage.WebserviceStorage;

import org.junit.Assert;
import org.junit.Test;

import org.ops4j.pax.exam.Configuration;
import static org.ops4j.pax.exam.CoreOptions.composite;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.ProbeBuilder;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.util.PathUtils;

import org.slf4j.LoggerFactory;

/*
 * Tests the framework core and the the Java API via the XMLCalabash based implementation.
 */
public class FrameworkCoreTest extends AbstractTest {
	
	@Inject
	JobManagerFactory jobManagerFactory;
	
	@Inject
	WebserviceStorage webserviceStorage;
	
	@Inject
	ScriptRegistry scriptRegistry;
	
	@Inject
	EventBusProvider eventBusProvider;
	
	@Test
	public void testCaughtError() throws IOException {
		Logger logger = (Logger)LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
		CollectLogMessages collectLog = new CollectLogMessages(logger.getLoggerContext(), Level.ERROR);
		logger.addAppender(collectLog);
		CollectMessages collectMessages = new CollectMessages();
		EventBus bus = eventBusProvider.get();
		bus.register(collectMessages);
		try {
			OutputPortReader resultPort = new OutputPortReader();
			Job job = newJob("catch-xproc-error",
			                 new XProcInput.Builder().build(),
			                 new XProcOutput.Builder().withOutput("result", resultPort).build());
			String id = job.getId().toString();
			waitForStatus(Job.Status.FAIL, job, 1000);
			Iterator<Reader> results = resultPort.read();
			Assert.assertTrue(results.hasNext());
			Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
			                    "<c:errors xmlns:c=\"http://www.w3.org/ns/xproc-step\">" +
			                      "<c:error name=\"!1.3.1.1\" type=\"p:error\" code=\"FOO\">" +
			                        "<message xmlns:px=\"http://www.daisy.org/ns/pipeline/xproc\" " +
			                                 "xmlns:d=\"http://www.daisy.org/ns/pipeline/data\">" +
			                          "foobar" +
			                        "</message>" +
			                      "</c:error>" +
			                    "</c:errors>",
			                    CharStreams.toString(results.next()));
			Assert.assertFalse(results.hasNext());
			Iterator<Message> messages = collectMessages.get(id);
			Assert.assertFalse(messages.hasNext());
			Iterator<ILoggingEvent> log = collectLog.get();
			Assert.assertFalse(log.hasNext());
		} finally {
			logger.detachAppender(collectLog);
			bus.unregister(collectMessages);
		}
	}
	
	@Test
	public void testUncaughtXProcError() {
		Logger logger = (Logger)LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
		CollectLogMessages collectLog = new CollectLogMessages(logger.getLoggerContext(), Level.ERROR);
		logger.addAppender(collectLog);
		CollectMessages collectMessages = new CollectMessages();
		EventBus bus = eventBusProvider.get();
		bus.register(collectMessages);
		try {
			Job job = newJob("xproc-error");
			String id = job.getId().toString();
			waitForStatus(Job.Status.ERROR, job, 1000);
			Iterator<Message> messages = collectMessages.get(id);
			int seq = 0;
			assertMessage(next(messages), seq++, Message.Level.ERROR, "FOO:foobar");
			assertMessage(next(messages), seq++, Message.Level.ERROR, "com.xmlcalabash.core.XProcException: foobar");
			Assert.assertFalse(messages.hasNext());
			Iterator<ILoggingEvent> log = collectLog.get();
			assertLogMessage(next(log), "com.xmlcalabash", Level.ERROR,
			                 Predicates.containsPattern("^\\Qbundle://\\E.+\\Q/module/xproc-error.xpl:0:FOO:foobar\\E$"));
			assertLogMessage(next(log), "org.daisy.pipeline.job.Job", Level.ERROR,
			                 Predicates.containsPattern("^\\Q" +
			                     "job finished with error state\n" +
			                     "java.lang.RuntimeException: com.xmlcalabash.core.XProcException: foobar\n" +
			                     "	at org.daisy.common.xproc.calabash.impl.CalabashXProcPipeline.run(CalabashXProcPipeline.java:\\E[0-9]+\\Q)\n" +
			                     "	at org.daisy.pipeline.job.Job.run(Job.java:\\E[0-9]+\\Q)\n" +
			                     "	at org.daisy.pipeline.job.impl.DefaultJobExecutionService$1.run(DefaultJobExecutionService.java:\\E[0-9]+\\Q)\n" +
			                     "	at java.lang.Thread.run(Thread.java:\\E[0-9]+\\Q)\n" +
			                     "Caused by: com.xmlcalabash.core.XProcException: foobar\n" +
			                     "	at com.xmlcalabash.library.Error.run(Error.java:\\E[0-9]+\\Q)\n" +
			                     "	at com.xmlcalabash.runtime.XAtomicStep.run(XAtomicStep.java:\\E[0-9]+\\Q)\n" +
			                     "	at com.xmlcalabash.runtime.XPipeline.doRun(XPipeline.java:\\E[0-9]+\\Q)\n" +
			                     "	at com.xmlcalabash.runtime.XPipeline.run(XPipeline.java:\\E[0-9]+\\Q)\n" +
			                     "	at org.daisy.common.xproc.calabash.impl.CalabashXProcPipeline.run(CalabashXProcPipeline.java:\\E[0-9]+\\Q)\n" +
			                     "	... 3 more\\E$"));
			Assert.assertFalse(log.hasNext());
		} finally {
			logger.detachAppender(collectLog);
			bus.unregister(collectMessages);
		}
	}
	
	@Test
	public void testCaughtXslTerminateError() throws IOException {
		Logger logger = (Logger)LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
		CollectLogMessages collectLog = new CollectLogMessages(logger.getLoggerContext(), Level.ERROR);
		logger.addAppender(collectLog);
		CollectMessages collectMessages = new CollectMessages();
		EventBus bus = eventBusProvider.get();
		bus.register(collectMessages);
		try {
			OutputPortReader resultPort = new OutputPortReader();
			Job job = newJob("catch-xslt-terminate-error",
			                 new XProcInput.Builder().build(),
			                 new XProcOutput.Builder().withOutput("result", resultPort).build());
			String id = job.getId().toString();
			waitForStatus(Job.Status.FAIL, job, 1000);
			Iterator<Reader> results = resultPort.read();
			Assert.assertTrue(results.hasNext());
			Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
			                    "<c:errors xmlns:c=\"http://www.w3.org/ns/xproc-step\">" +
			                      "<c:error>" +
			                        "Runtime Error" +
			                      "</c:error>" +
			                    "</c:errors>",
			                    CharStreams.toString(results.next()));
			Assert.assertFalse(results.hasNext());
			Iterator<Message> messages = collectMessages.get(id);
			int seq = 0;
			assertMessage(next(messages), seq++, Message.Level.INFO, "Runtime Error");
			Assert.assertFalse(messages.hasNext());
			Iterator<ILoggingEvent> log = collectLog.get();
			Assert.assertFalse(log.hasNext());
		} finally {
			logger.detachAppender(collectLog);
			bus.unregister(collectMessages);
		}
	}
	
	@Test
	public void testUncaughtXslTerminateError() {
		Logger logger = (Logger)LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
		CollectLogMessages collectLog = new CollectLogMessages(logger.getLoggerContext(), Level.ERROR);
		logger.addAppender(collectLog);
		CollectMessages collectMessages = new CollectMessages();
		EventBus bus = eventBusProvider.get();
		bus.register(collectMessages);
		try {
			Job job = newJob("xslt-terminate-error");
			String id = job.getId().toString();
			waitForStatus(Job.Status.ERROR, job, 1000);
			Iterator<Message> messages = collectMessages.get(id);
			int seq = 0;
			assertMessage(next(messages), seq++, Message.Level.INFO, "Runtime Error");
			assertMessage(next(messages), seq++, Message.Level.ERROR, "XTMM9000:Processing terminated by xsl:message at line -1 in null");
			assertMessage(next(messages), seq++, Message.Level.ERROR, "Processing terminated by xsl:message at line -1 in null");
			assertMessage(next(messages), seq++, Message.Level.ERROR, "net.sf.saxon.s9api.SaxonApiException: Processing terminated by xsl:message at line -1 in null");
			Assert.assertFalse(messages.hasNext());
			Iterator<ILoggingEvent> log = collectLog.get();
			assertLogMessage(next(log), "com.xmlcalabash.runtime.XAtomicStep", Level.ERROR,
			                 "XTMM9000:Processing terminated by xsl:message at line -1 in null");
			assertLogMessage(next(log), "com.xmlcalabash.runtime.XAtomicStep", Level.ERROR,
			                 "Processing terminated by xsl:message at line -1 in null");
			assertLogMessage(next(log), "org.daisy.pipeline.job.Job", Level.ERROR,
			                 Predicates.containsPattern("^\\Q" +
			                     "job finished with error state\n" +
			                     "java.lang.RuntimeException: net.sf.saxon.s9api.SaxonApiException: Processing terminated by xsl:message at line -1 in null\n" +
			                     "	at org.daisy.common.xproc.calabash.impl.CalabashXProcPipeline.run(CalabashXProcPipeline.java:\\E[0-9]+\\Q)\n" +
			                     "	at org.daisy.pipeline.job.Job.run(Job.java:\\E[0-9]+\\Q)\n" +
			                     "	at org.daisy.pipeline.job.impl.DefaultJobExecutionService$1.run(DefaultJobExecutionService.java:\\E[0-9]+\\Q)\n" +
			                     "	at java.lang.Thread.run(Thread.java:\\E[0-9]+\\Q)\n" +
			                     "Caused by: net.sf.saxon.s9api.SaxonApiException: Processing terminated by xsl:message at line -1 in null\n" +
			                     "	at net.sf.saxon.s9api.XsltTransformer.transform(XsltTransformer.java:\\E[0-9]+\\Q)\n" +
			                     "	at com.xmlcalabash.library.XSLT.run(XSLT.java:\\E[0-9]+\\Q)\n" +
			                     "	at com.xmlcalabash.runtime.XAtomicStep.run(XAtomicStep.java:\\E[0-9]+\\Q)\n" +
			                     "	at com.xmlcalabash.runtime.XPipeline.doRun(XPipeline.java:\\E[0-9]+\\Q)\n" +
			                     "	at com.xmlcalabash.runtime.XPipeline.run(XPipeline.java:\\E[0-9]+\\Q)\n" +
			                     "	at org.daisy.common.xproc.calabash.impl.CalabashXProcPipeline.run(CalabashXProcPipeline.java:\\E[0-9]+\\Q)\n" +
			                     "	... 3 more\n" +
			                     "Caused by: net.sf.saxon.expr.instruct.TerminationException: Processing terminated by xsl:message at line -1 in null\n" +
			                     "	at net.sf.saxon.expr.instruct.Message.processLeavingTail(Message.java:\\E[0-9]+\\Q)\n" +
			                     "	at net.sf.saxon.expr.instruct.Template.expand(Template.java:\\E[0-9]+\\Q)\n" +
			                     "	at net.sf.saxon.Controller.transformDocument(Controller.java:\\E[0-9]+\\Q)\n" +
			                     "	at net.sf.saxon.Controller.transform(Controller.java:\\E[0-9]+\\Q)\n" +
			                     "	at net.sf.saxon.s9api.XsltTransformer.transform(XsltTransformer.java:\\E[0-9]+\\Q)\n" +
			                     "	... 8 more\\E$"));
			Assert.assertFalse(log.hasNext());
		} finally {
			logger.detachAppender(collectLog);
			bus.unregister(collectMessages);
		}
	}
	
	@Test
	public void testUncaughtJavaStepError() {
		Logger logger = (Logger)LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
		CollectLogMessages collectLog = new CollectLogMessages(logger.getLoggerContext(), Level.ERROR);
		logger.addAppender(collectLog);
		CollectMessages collectMessages = new CollectMessages();
		EventBus bus = eventBusProvider.get();
		bus.register(collectMessages);
		try {
			Job job = newJob("java-step-runtime-error");
			String id = job.getId().toString();
			waitForStatus(Job.Status.ERROR, job, 1000);
			Iterator<Message> messages = collectMessages.get(id);
			int seq = 0;
			assertMessage(next(messages), seq++, Message.Level.ERROR, "java.lang.RuntimeException: foobar");
			Assert.assertFalse(messages.hasNext());
			Iterator<ILoggingEvent> log = collectLog.get();
			assertLogMessage(next(log), "org.daisy.pipeline.job.Job", Level.ERROR,
			                 Predicates.containsPattern("^\\Q" +
			                     "job finished with error state\n" +
			                     "java.lang.RuntimeException: java.lang.RuntimeException: foobar\n" +
			                     "	at org.daisy.common.xproc.calabash.impl.CalabashXProcPipeline.run(CalabashXProcPipeline.java:\\E[0-9]+\\Q)\n" +
			                     "	at org.daisy.pipeline.job.Job.run(Job.java:\\E[0-9]+\\Q)\n" +
			                     "	at org.daisy.pipeline.job.impl.DefaultJobExecutionService$1.run(DefaultJobExecutionService.java:\\E[0-9]+\\Q)\n" +
			                     "	at java.lang.Thread.run(Thread.java:\\E[0-9]+\\Q)\n" +
			                     "Caused by: java.lang.RuntimeException: foobar\n" +
			                     "	at JavaStep.run(JavaStep.java:\\E[0-9]+\\Q)\n" +
			                     "	at com.xmlcalabash.runtime.XAtomicStep.run(XAtomicStep.java:\\E[0-9]+\\Q)\n" +
			                     "	at com.xmlcalabash.runtime.XPipeline.doRun(XPipeline.java:\\E[0-9]+\\Q)\n" +
			                     "	at com.xmlcalabash.runtime.XPipeline.run(XPipeline.java:\\E[0-9]+\\Q)\n" +
			                     "	at org.daisy.common.xproc.calabash.impl.CalabashXProcPipeline.run(CalabashXProcPipeline.java:\\E[0-9]+\\Q)\n" +
			                     "	... 3 more\\E$"));
			Assert.assertFalse(log.hasNext());
		} finally {
			logger.detachAppender(collectLog);
			bus.unregister(collectMessages);
		}
	}
	
	@Test
	public void testUncaughtJavaFunctionError() {
		Logger logger = (Logger)LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
		CollectLogMessages collectLog = new CollectLogMessages(logger.getLoggerContext(), Level.ERROR);
		logger.addAppender(collectLog);
		CollectMessages collectMessages = new CollectMessages();
		EventBus bus = eventBusProvider.get();
		bus.register(collectMessages);
		try {
			Job job = newJob("java-function-runtime-error");
			String id = job.getId().toString();
			waitForStatus(Job.Status.ERROR, job, 1000);
			Iterator<Message> messages = collectMessages.get(id);
			int seq = 0;
			assertMessage(next(messages), seq++, Message.Level.ERROR, "java.lang.RuntimeException: foobar");
			Assert.assertFalse(messages.hasNext());
			Iterator<ILoggingEvent> log = collectLog.get();
			assertLogMessage(next(log), "org.daisy.pipeline.job.Job", Level.ERROR,
			                 Predicates.containsPattern("^\\Q" +
			                     "job finished with error state\n" +
			                     "java.lang.RuntimeException: java.lang.RuntimeException: foobar\n" +
			                     "	at org.daisy.common.xproc.calabash.impl.CalabashXProcPipeline.run(CalabashXProcPipeline.java:\\E[0-9]+\\Q)\n" +
			                     "	at org.daisy.pipeline.job.Job.run(Job.java:\\E[0-9]+\\Q)\n" +
			                     "	at org.daisy.pipeline.job.impl.DefaultJobExecutionService$1.run(DefaultJobExecutionService.java:\\E[0-9]+\\Q)\n" +
			                     "	at java.lang.Thread.run(Thread.java:\\E[0-9]+\\Q)\n" +
			                     "Caused by: java.lang.RuntimeException: foobar\n" +
			                     "	at JavaFunction$1.call(JavaFunction.java:50)\n" +
			                     "	at net.sf.saxon.functions.IntegratedFunctionCall.iterate(IntegratedFunctionCall.java:\\E[0-9]+\\Q)\n" +
			                     "	at net.sf.saxon.expr.Expression.evaluateItem(Expression.java:\\E[0-9]+\\Q)\n" +
			                     "	at net.sf.saxon.expr.Expression.evaluateAsString(Expression.java:\\E[0-9]+\\Q)\n" +
			                     "	at net.sf.saxon.expr.instruct.SimpleNodeConstructor.processLeavingTail(SimpleNodeConstructor.java:\\E[0-9]+\\Q)\n" +
			                     "	at net.sf.saxon.expr.instruct.ValueOf.processLeavingTail(ValueOf.java:\\E[0-9]+\\Q)\n" +
			                     "	at net.sf.saxon.expr.instruct.Template.expand(Template.java:\\E[0-9]+\\Q)\n" +
			                     "	at net.sf.saxon.Controller.transformDocument(Controller.java:\\E[0-9]+\\Q)\n" +
			                     "	at net.sf.saxon.Controller.transform(Controller.java:\\E[0-9]+\\Q)\n" +
			                     "	at net.sf.saxon.s9api.XsltTransformer.transform(XsltTransformer.java:\\E[0-9]+\\Q)\n" +
			                     "	at com.xmlcalabash.library.XSLT.run(XSLT.java:\\E[0-9]+\\Q)\n" +
			                     "	at com.xmlcalabash.runtime.XAtomicStep.run(XAtomicStep.java:\\E[0-9]+\\Q)\n" +
			                     "	at com.xmlcalabash.runtime.XPipeline.doRun(XPipeline.java:\\E[0-9]+\\Q)\n" +
			                     "	at com.xmlcalabash.runtime.XPipeline.run(XPipeline.java:\\E[0-9]+\\Q)\n" +
			                     "	at org.daisy.common.xproc.calabash.impl.CalabashXProcPipeline.run(CalabashXProcPipeline.java:\\E[0-9]+\\Q)\n" +
			                     "	... 3 more\\E$"));
			Assert.assertFalse(log.hasNext());
		} finally {
			logger.detachAppender(collectLog);
			bus.unregister(collectMessages);
		}
	}
	
	@Test
	public void testXslWarning() {
		Logger logger = (Logger)LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
		CollectLogMessages collectLog = new CollectLogMessages(logger.getLoggerContext(), Level.ERROR);
		logger.addAppender(collectLog);
		CollectMessages collectMessages = new CollectMessages();
		EventBus bus = eventBusProvider.get();
		bus.register(collectMessages);
		try {
			Job job = newJob("xslt-warning");
			String id = job.getId().toString();
			waitForStatus(Job.Status.DONE, job, 1000);
			Iterator<Message> messages = collectMessages.get(id);
			int seq = 0;
			assertMessage(next(messages), seq++, Message.Level.ERROR,
			              "err:XTRE0540:Ambiguous rule match for /hello\n" +
			              "Matches both \"element(Q{}hello)\" on line -1 of bundle://50.0:1/module/xslt-warning.xpl\n" +
			              "and \"element(Q{}hello)\" on line -1 of bundle://50.0:1/module/xslt-warning.xpl");
			Assert.assertFalse(messages.hasNext());
			Iterator<ILoggingEvent> log = collectLog.get();
			assertLogMessage(next(log), "com.xmlcalabash", Level.ERROR,
			                 "err:XTRE0540:Ambiguous rule match for /hello\n" +
			                 "Matches both \"element(Q{}hello)\" on line -1 of bundle://50.0:1/module/xslt-warning.xpl\n" +
			                 "and \"element(Q{}hello)\" on line -1 of bundle://50.0:1/module/xslt-warning.xpl");
			Assert.assertFalse(log.hasNext());
		} finally {
			logger.detachAppender(collectLog);
			bus.unregister(collectMessages);
		}
	}
	
	Job newJob(String scriptId) {
		return newJob(scriptId, new XProcInput.Builder().build(), new XProcOutput.Builder().build());
	}
	
	Job newJob(String scriptId, XProcInput input, XProcOutput output) {
		Client client = webserviceStorage.getClientStorage().defaultClient();
		JobManager jobManager = jobManagerFactory.createFor(client);
		XProcScriptService script = scriptRegistry.getScript(scriptId);
		Assert.assertNotNull("The " + scriptId + " script should exist", script);
		return jobManager.newJob(BoundXProcScript.from(script.load(), input, output))
		                 .isMapping(true)
		                 .withNiceName("nice")
		                 .build()
		                 .get();
	}
	
	static void waitForStatus(Job.Status status, Job job, long timeout) {
		long waited = 0L;
		while (job.getStatus() != status) {
			if (job.getStatus() == Job.Status.ERROR) {
				throw new RuntimeException("Job errored while waiting for another status");
			}
			try {
				Thread.sleep(500);
				waited += 500;
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			if (waited > timeout) {
				throw new RuntimeException("waitForStatus " + status + " timed out (last status was " + job.getStatus() + ")");
			}
		}
	}
	
	static <T> Optional<T> next(Iterator<T> iterator) {
		if (iterator.hasNext())
			return Optional.<T>of(iterator.next());
		else
			return Optional.<T>absent();
	}
	
	static void assertMessage(Optional<Message> message, int expectedSequence, Message.Level expectedLevel, String expectedText) {
		Assert.assertTrue("message does not exist", message.isPresent());
		Message m = message.get();
		Assert.assertEquals("message sequence number does not match", expectedSequence, m.getSequence());
		Assert.assertEquals("message level does not match", expectedLevel, m.getLevel());
		Assert.assertEquals("message text does not match", expectedText, m.getText());
	}
	
	static void assertLogMessage(Optional<ILoggingEvent> message,
	                             String expectedLoggerName, Level expectedLevel, String expectedText) {
		assertLogMessage(message, expectedLoggerName, expectedLevel, Predicates.equalTo(expectedText));
	}
	
	static void assertLogMessage(Optional<ILoggingEvent> message,
	                             String expectedLoggerName, Level expectedLevel, Predicate<? super String> expectedText) {
		Assert.assertTrue("message does not exist", message.isPresent());
		ILoggingEvent m = message.get();
		Assert.assertEquals("logger name does not match", expectedLoggerName, m.getLoggerName());
		Assert.assertEquals("message level does not match", expectedLevel, m.getLevel());
		StringBuilder text = new StringBuilder(); {
			text.append(m.getFormattedMessage());
			ThrowableProxy throwable = (ThrowableProxy)m.getThrowableProxy();
			if (throwable != null) {
				StringWriter trace = new StringWriter();
				throwable.getThrowable().printStackTrace(new PrintWriter(trace));
				text.append("\n" + trace);
				// remove newline
				text.setLength(text.length() - 1);
			}
		}
		Assert.assertTrue("message text does not match", expectedText.apply(text.toString()));
	}
	
	static void printLogMessage(ILoggingEvent message, PrintStream out) {
		String s = "";
		switch(message.getLevel().toInt()) {
		case Level.TRACE_INT:
			s += "TRACE: ";
			break;
		case Level.DEBUG_INT:
			s += "DEBUG: ";
			break;
		case Level.INFO_INT:
			s += "INFO:  ";
			break;
		case Level.WARN_INT:
			s += "WARN:  ";
			break;
		case Level.ERROR_INT:
			s += "ERROR: ";
			break;
		case Level.ALL_INT:
		case Level.OFF_INT:
		default:
		}
		s += message.getLoggerName() + " - ";
		s += message.getFormattedMessage();
		out.println(s);
		ThrowableProxy throwable = (ThrowableProxy)message.getThrowableProxy();
		if (throwable != null)
			throwable.getThrowable().printStackTrace(out);
	}
	
	// FIXME: can dependencies on modules-registry, webservice-utils and framework-volatile be eliminated?
	@Override
	public String[] testDependencies() {
		return new String[]{
			"com.google.guava:guava:?",
			"org.daisy.libs:com.xmlcalabash:?",
			"org.daisy.libs:saxon-he:?",
			"org.slf4j:slf4j-api:?",
			"org.daisy.pipeline:common-utils:?",
			"org.daisy.pipeline:framework-core:?",
			"org.daisy.pipeline:xpath-registry:?",
			"org.daisy.pipeline:xproc-api:?",
			"org.daisy.pipeline:modules-registry:?",
			"org.apache.httpcomponents:httpclient-osgi:?",
			"org.apache.httpcomponents:httpcore-osgi:?",
			"org.daisy.libs:jing:?",
			"org.daisy.pipeline:framework-volatile:?"
		};
	}
	
	static final File PIPELINE_BASE = new File(new File(PathUtils.getBaseDir()), "target/tmp");
	static final File PIPELINE_DATA = new File(PIPELINE_BASE, "data");

	@Override @Configuration
	public Option[] config() {
		return options(
			composite(super.config()),
			systemProperty("org.daisy.pipeline.iobase").value(new File(PIPELINE_DATA, "jobs").getAbsolutePath()));
	}
	
	@ProbeBuilder
	public TestProbeBuilder probeConfiguration(TestProbeBuilder probe) {
		probe.setHeader("Bundle-Name", "Test module");
		probe.setHeader("Service-Component", "OSGI-INF/script.xml,"
		                                   + "OSGI-INF/java-step.xml,"
		                                   + "OSGI-INF/java-function.xml");
		return probe;
	}
	
	static class CollectMessages {
		final Map<String,List<Message>> messages = new HashMap<String,List<Message>>();
		@Subscribe
		public void add(Message msg) {
			String jobid = msg.getJobId();
			List<Message> list = messages.get(jobid);
			if (list == null) {
				list = new ArrayList<Message>();
				messages.put(jobid, list);
			}
			list.add(msg);
		}
		public Iterator<Message> get(String jobId) {
			if (messages.containsKey(jobId))
				return messages.get(jobId).iterator();
			else
				return Collections.<Message>emptyIterator();
		}
	}
	
	static class CollectLogMessages extends AppenderBase<ILoggingEvent> {
		final List<ILoggingEvent> log = new ArrayList<ILoggingEvent>();
		final Level threshold;
		CollectLogMessages(LoggerContext context, Level threshold) {
			super();
			this.threshold = threshold;
			setContext(context);
			start();
		}
		public void append(ILoggingEvent event) {
			if (event.getLevel().toInt() >= threshold.toInt()) {
				printLogMessage(event, System.out);
				log.add(event);
			}
		}
		Iterator<ILoggingEvent> get() {
			return log.iterator();
		}
	}
	
	static class OutputPortReader implements Supplier<Result> {
		final List<ByteArrayOutputStream> docs = new ArrayList<ByteArrayOutputStream>();
		public Result get() {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			docs.add(os);
			return new StreamResult(os);
		}
		public Iterator<Reader> read() {
			return Iterators.<ByteArrayOutputStream,Reader>transform(
				docs.iterator(),
				new Function<ByteArrayOutputStream,Reader>() {
					public Reader apply(ByteArrayOutputStream os) {
						try {
							return new InputStreamReader(new ByteArrayInputStream(os.toByteArray()), "UTF-8"); }
						catch (UnsupportedEncodingException e) {
							throw new RuntimeException(e); }}});
		}
	}
}
