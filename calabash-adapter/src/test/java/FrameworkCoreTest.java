import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.stream.Stream;
import javax.inject.Inject;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.AppenderBase;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.google.common.io.CharStreams;

import org.daisy.common.messaging.Message;
import org.daisy.common.messaging.MessageAccessor;
import org.daisy.common.messaging.ProgressMessage;
import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.JobFactory;
import org.daisy.pipeline.junit.AbstractTest;
import org.daisy.pipeline.script.BoundScript;
import org.daisy.pipeline.script.ScriptRegistry;
import org.daisy.pipeline.script.ScriptService;

import org.junit.Assert;
import org.junit.Test;

import org.ops4j.pax.exam.Configuration;
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
	public JobFactory jobFactory;
	
	@Inject
	public ScriptRegistry scriptRegistry;
	
	@Test
	public void testCaughtError() throws IOException {
		Logger logger = (Logger)LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
		CollectLogMessages collectLog = new CollectLogMessages(logger.getLoggerContext(), Level.ERROR);
		logger.addAppender(collectLog);
		try (Job job = newJob("catch-xproc-error")) {
			waitForStatus(Job.Status.FAIL, job, 2000);
			Iterator<String> results = Iterators.transform(
				job.getResults().getResults("result").iterator(),
				r -> {
					try {
						return CharStreams.toString(new InputStreamReader(r.asStream(), "UTF-8")); }
					catch (IOException e) {
						throw new RuntimeException(e); }});
			Assert.assertTrue(results.hasNext());
			String errorXml = results.next();
			System.out.println(errorXml);
			Assert.assertTrue(
				Predicates.containsPattern(
					"^\\Q" +
					"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
					"<c:errors xmlns:c=\"http://www.w3.org/ns/xproc-step\">" +
					  "<c:error code=\"FOO\" name=\"!1.2\" type=\"p:error\" " +
					           "href=\"\\E.+\\Q/module/error.xpl\" line=\"8\" column=\"25\">" +
					    "<message xmlns:px=\"http://www.daisy.org/ns/pipeline/xproc\">" +
					      "foobar" +
					    "</message>" +
					    "<px:location xmlns:px=\"http://www.daisy.org/ns/pipeline/xproc\">" +
					      "<px:file href=\"\\E.+\\Q/module/error.xpl\" line=\"8\" column=\"25\"/>" +
					      "<px:file href=\"\\E.+\\Q/module/catch-xproc-error.xpl\" line=\"19\" column=\"24\"/>" +
					      "<px:file href=\"\\E.+\\Q/module/catch-xproc-error.xpl\" line=\"18\" column=\"18\"/>" +
					      "<px:file href=\"\\E.+\\Q/module/catch-xproc-error.xpl\" line=\"17\" column=\"12\"/>" +
					    "</px:location>" +
					  "</c:error>" +
					"</c:errors>" +
					"\\E$"
				).apply(errorXml));
			Assert.assertFalse(results.hasNext());
			MessageAccessor accessor = job.getMonitor().getMessageAccessor();
			Iterator<Message> messages = printMessages(accessor.getAll().iterator());
			try {
				Assert.assertFalse(messages.hasNext());
			} catch (Throwable e) {
				// print remaining messages
				sink(messages);
				throw e;
			}
			Iterator<ILoggingEvent> log = collectLog.get();
			Assert.assertFalse(log.hasNext());
		} finally {
			logger.detachAppender(collectLog);
		}
	}
	
	@Test
	public void testUncaughtXProcError() {
		Logger logger = (Logger)LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
		CollectLogMessages collectLog = new CollectLogMessages(logger.getLoggerContext(), Level.ERROR);
		logger.addAppender(collectLog);
		try (Job job = newJob("xproc-error")) {
			waitForStatus(Job.Status.ERROR, job, 2000);
			MessageAccessor accessor = job.getMonitor().getMessageAccessor();
			Iterator<Message> messages = printMessages(accessor.getAll().iterator());
			try {
				int seq = 0;
				seq++; // px:error
				seq++; // p:error
				assertMessage(next(messages), seq++, Message.Level.ERROR, "foobar (Please see detailed log for more info.)");
				Assert.assertFalse(messages.hasNext());
			} catch (Throwable e) {
				// print remaining messages
				sink(messages);
				throw e;
			}
			Iterator<ILoggingEvent> log = collectLog.get();
			assertLogMessage(next(log), "org.daisy.pipeline.job.Job", Level.ERROR,
			                 "job finished with error state\n" +
			                 "[FOO] foobar\n" +
			                 "	at {http://www.w3.org/ns/xproc}error(error.xpl:8)\n" +
			                 "	at {http://www.daisy.org/ns/pipeline/xproc}error(xproc-error.xpl:10)");
			Assert.assertFalse(log.hasNext());
		} finally {
			logger.detachAppender(collectLog);
		}
	}
	
	@Test
	public void testUncaughtXProcErrorInsideCxEval() {
		Logger logger = (Logger)LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
		CollectLogMessages collectLog = new CollectLogMessages(logger.getLoggerContext(), Level.ERROR);
		logger.addAppender(collectLog);
		try (Job job = newJob("cx-eval-error")) {
			waitForStatus(Job.Status.ERROR, job, 1000);
			MessageAccessor accessor = job.getMonitor().getMessageAccessor();
			Iterator<Message> messages = printMessages(accessor.getAll().iterator());
			try {
				int seq = 0;
				seq++; // cx:eval
				seq++; // px:error
				seq++; // p:error
				assertMessage(next(messages), seq++, Message.Level.ERROR, "foobar (Please see detailed log for more info.)");
				Assert.assertFalse(messages.hasNext());
			} catch (Throwable e) {
				// print remaining messages
				sink(messages);
				throw e;
			}
			Iterator<ILoggingEvent> log = collectLog.get();
			assertLogMessage(next(log), "org.daisy.pipeline.job.Job", Level.ERROR,
			                 "job finished with error state\n" +
			                 "[FOO] foobar\n" +
			                 "	at {http://www.w3.org/ns/xproc}error(error.xpl:8)\n" +
			                 "	at {http://www.daisy.org/ns/pipeline/xproc}error(xproc-error.xpl:10)\n" +
			                 "	at {http://xmlcalabash.com/ns/extensions}eval(cx-eval-error.xpl:15)");
			Assert.assertFalse(log.hasNext());
		} finally {
			logger.detachAppender(collectLog);
		}
	}
	
	@Test
	public void testCaughtXslTerminateError() throws IOException {
		Logger logger = (Logger)LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
		CollectLogMessages collectLog = new CollectLogMessages(logger.getLoggerContext(), Level.ERROR);
		logger.addAppender(collectLog);
		try (Job job = newJob("catch-xslt-terminate-error")) {
			waitForStatus(Job.Status.FAIL, job, 1000);
			Iterator<String> results = Iterators.transform(
				job.getResults().getResults("result").iterator(),
				r -> {
					try {
						return CharStreams.toString(new InputStreamReader(r.asStream(), "UTF-8")); }
					catch (IOException e) {
						throw new RuntimeException(e); }});
			Assert.assertTrue(results.hasNext());
			String errorXml = results.next();
			System.out.println(errorXml);
			Assert.assertTrue(
				Predicates.containsPattern(
					"^\\Q" +
					"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
					"<c:errors xmlns:c=\"http://www.w3.org/ns/xproc-step\">" +
					  "<c:error href=\"\\E.+\\Q/module/catch-xslt-terminate-error.xpl\" line=\"25\">" +
					    "Runtime Error" +
					    "<px:location xmlns:px=\"http://www.daisy.org/ns/pipeline/xproc\">" +
					      "<px:file href=\"\\E.+\\Q/module/catch-xslt-terminate-error.xpl\" line=\"25\"/>" +
					      "<px:file href=\"\\E.+\\Q/module/catch-xslt-terminate-error.xpl\" line=\"17\" column=\"43\"/>" +
					      "<px:file href=\"\\E.+\\Q/module/catch-xslt-terminate-error.xpl\" line=\"16\" column=\"18\"/>" +
					      "<px:file href=\"\\E.+\\Q/module/catch-xslt-terminate-error.xpl\" line=\"15\" column=\"12\"/>" +
					    "</px:location>" +
					  "</c:error>" +
					"</c:errors>" +
					"\\E$"
				).apply(errorXml));
			Assert.assertFalse(results.hasNext());
			MessageAccessor accessor = job.getMonitor().getMessageAccessor();
			Iterator<Message> messages = printMessages(accessor.getAll().iterator());
			try {
				Assert.assertFalse(messages.hasNext());
			} catch (Throwable e) {
				// print remaining messages
				sink(messages);
				throw e;
			}
			Iterator<ILoggingEvent> log = collectLog.get();
			Assert.assertFalse(log.hasNext());
		} finally {
			logger.detachAppender(collectLog);
		}
	}
	
	@Test
	public void testUncaughtXslTerminateError() {
		Logger logger = (Logger)LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
		CollectLogMessages collectLog = new CollectLogMessages(logger.getLoggerContext(), Level.ERROR);
		logger.addAppender(collectLog);
		try (Job job = newJob("xslt-terminate-error")) {
			waitForStatus(Job.Status.ERROR, job, 1000);
			MessageAccessor accessor = job.getMonitor().getMessageAccessor();
			Iterator<Message> messages = printMessages(accessor.getAll().iterator());
			try {
				int seq = 0;
				seq++; // p:xslt
				assertMessage(next(messages), seq++, Message.Level.ERROR, "Runtime Error (Please see detailed log for more info.)");
				Assert.assertFalse(messages.hasNext());
			} catch (Throwable e) {
				// print remaining messages
				sink(messages);
				throw e;
			}
			Iterator<ILoggingEvent> log = collectLog.get();
			assertLogMessage(next(log), "org.daisy.pipeline.job.Job", Level.ERROR,
			                 "job finished with error state\n" +
			                 "Runtime Error\n" +
			                 "	at xsl:message(xslt-terminate-error.xpl:16)\n" +
			                 "	at {http://www.w3.org/ns/xproc}xslt(xslt-terminate-error.xpl:8)");
			Assert.assertFalse(log.hasNext());
		} finally {
			logger.detachAppender(collectLog);
		}
	}
	
	@Test
	public void testUncaughtJavaStepError() {
		Logger logger = (Logger)LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
		CollectLogMessages collectLog = new CollectLogMessages(logger.getLoggerContext(), Level.ERROR);
		logger.addAppender(collectLog);
		try (Job job = newJob("java-step-runtime-error")) {
			waitForStatus(Job.Status.ERROR, job, 1000);
			MessageAccessor accessor = job.getMonitor().getMessageAccessor();
			Iterator<Message> messages = printMessages(accessor.getAll().iterator());
			try {
				int seq = 0;
				seq++; // px:java-step
				assertMessage(next(messages), seq++, Message.Level.INFO, "going to throw an exception");
				assertMessage(next(messages), seq++, Message.Level.ERROR, "foobar (Please see detailed log for more info.)");
				Assert.assertFalse(messages.hasNext());
			} catch (Throwable e) {
				// print remaining messages
				sink(messages);
				throw e;
			}
			Iterator<ILoggingEvent> log = collectLog.get();
			assertLogMessage(next(log), "org.daisy.pipeline.job.Job", Level.ERROR,
			                 "job finished with error state\n" +
			                 "foobar\n" +
			                 "	at {http://www.daisy.org/ns/pipeline/xproc}java-step(java-step-runtime-error.xpl:14)\n" +
			                 "Caused by: foobar\n" +
			                 "	at JavaStep.run(JavaStep.java:59)\n" +
			                 "	at {http://www.daisy.org/ns/pipeline/xproc}java-step(java-step-runtime-error.xpl:14)");
			Assert.assertFalse(log.hasNext());
		} finally {
			logger.detachAppender(collectLog);
		}
	}
	
	@Test
	public void testUncaughtJavaFunctionError() {
		Logger logger = (Logger)LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
		CollectLogMessages collectLog = new CollectLogMessages(logger.getLoggerContext(), Level.ERROR);
		logger.addAppender(collectLog);
		try (Job job = newJob("java-function-runtime-error")) {
			waitForStatus(Job.Status.ERROR, job, 1000);
			MessageAccessor accessor = job.getMonitor().getMessageAccessor();
			Iterator<Message> messages = printMessages(accessor.getAll().iterator());
			try {
				int seq = 0;
				seq++; // p:xslt
				assertMessage(next(messages), seq++, Message.Level.INFO, "inside pf:java-function");
				assertMessage(next(messages), seq++, Message.Level.INFO, "going to throw an exception");
				assertMessage(next(messages), seq++, Message.Level.ERROR,
				              "Unexpected error in {http://www.daisy.org/ns/pipeline/functions}java-function " +
				              "(Please see detailed log for more info.)");
				Assert.assertFalse(messages.hasNext());
			} catch (Throwable e) {
				// print remaining messages
				sink(messages);
				throw e;
			}
			Iterator<ILoggingEvent> log = collectLog.get();
			assertLogMessage(next(log), "org.daisy.pipeline.job.Job", Level.ERROR,
			                 m -> m.startsWith(
			                     "job finished with error state\n" +
			                     "[MYERR] Unexpected error in {http://www.daisy.org/ns/pipeline/functions}java-function\n" +
			                     "	at xsl:value-of/@select(java-function-runtime-error.xpl:26)\n" +
			                     "	at {http://www.daisy.org/ns/pipeline/functions}user-function()(java-function-runtime-error.xpl:23)\n" +
			                     "	at xsl:call-template name=\"b\"(java-function-runtime-error.xpl:20)\n" +
			                     "	at xsl:call-template name=\"a\"(java-function-runtime-error.xpl:17)\n" +
			                     "	at {http://www.w3.org/ns/xproc}xslt(java-function-runtime-error.xpl:9)\n" +
			                     "Caused by: foobar\n" +
			                     "	at JavaFunction$1.call(JavaFunction.java:62)\n"));
			Assert.assertFalse(log.hasNext());
		} finally {
			logger.detachAppender(collectLog);
		}
	}
	
	@Test
	public void testXslWarning() {
		Logger logger = (Logger)LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
		CollectLogMessages collectLog = new CollectLogMessages(logger.getLoggerContext(), Level.ERROR);
		logger.addAppender(collectLog);
		try (Job job = newJob("xslt-warning")) {
			waitForStatus(Job.Status.SUCCESS, job, 1000);
			MessageAccessor accessor = job.getMonitor().getMessageAccessor();
			Iterator<Message> messages = printMessages(accessor.getAll().iterator());
			try {
				int seq = 0;
				seq++; // p:xslt
				assertMessage(next(messages), seq++, Message.Level.WARNING,
				              Predicates.containsPattern("^\\Q" +
				                  "err:XTDE0540:Ambiguous rule match for /hello\n" +
				                  "Matches both \"element(Q{}hello)\" on line 21 of \\E.+\\Q/module/xslt-warning.xpl\n" +
				                  "and \"element(Q{}hello)\" on line 17 of \\E.+\\Q/module/xslt-warning.xpl\\E$"));
				Assert.assertFalse(messages.hasNext());
			} catch (Throwable e) {
				// print remaining messages
				sink(messages);
				throw e;
			}
			Iterator<ILoggingEvent> log = collectLog.get();
			Assert.assertFalse(log.hasNext());
		} finally {
			logger.detachAppender(collectLog);
		}
	}
	
	@Test
	public void testXProcWarning() {
		Logger logger = (Logger)LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
		CollectLogMessages collectLog = new CollectLogMessages(logger.getLoggerContext(), Level.ERROR);
		logger.addAppender(collectLog);
		try (Job job = newJob("xproc-warning")) {
			waitForStatus(Job.Status.SUCCESS, job, 1000);
			MessageAccessor accessor = job.getMonitor().getMessageAccessor();
			Iterator<Message> messages = printMessages(accessor.getAll().iterator());
			try {
				int seq = 0;
				assertMessage(next(messages), seq++, Message.Level.WARNING, "Hello WORLD!");
				Assert.assertFalse(messages.hasNext());
			} catch (Throwable e) {
				// print remaining messages
				sink(messages);
				throw e;
			}
			Iterator<ILoggingEvent> log = collectLog.get();
			Assert.assertFalse(log.hasNext());
		} finally {
			logger.detachAppender(collectLog);
		}
	}
	
	@Test
	public void testProgressMessages() throws InterruptedException, ExecutionException {
		Logger logger = (Logger)LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
		CollectLogMessages collectLog = new CollectLogMessages(logger.getLoggerContext(), Level.ERROR);
		logger.addAppender(collectLog);
		try (Job job = newJob("progress-messages")) {
			MessageAccessor accessor = job.getMonitor().getMessageAccessor();
			Runnable poller = new JobPoller(job, Job.Status.SUCCESS, 200, 3000) {
				BigDecimal lastProgress = BigDecimal.ZERO;
				Iterator<BigDecimal> mustSee = stream(".125", ".375", ".9").map(d -> new BigDecimal(d)).iterator();
				BigDecimal mustSeeNext = mustSee.next();
				List<BigDecimal> seen = new ArrayList<BigDecimal>();
				@Override
				void performAction(Job.Status status) {
					BigDecimal progress = accessor.getProgress();
					if (progress.compareTo(lastProgress) != 0) {
						Assert.assertTrue("Progress must be monotonic non-decreasing", progress.compareTo(lastProgress) >= 0);
						if (mustSeeNext != null) {
							if (progress.compareTo(mustSeeNext) == 0) {
								seen.clear();
								mustSeeNext = mustSee.hasNext() ? mustSee.next() : null;
							} else {
								seen.add(progress);
								Assert.assertTrue("Expected " + mustSeeNext + " but got " + seen, progress.compareTo(mustSeeNext) < 0);
							}
						}
						lastProgress = progress;
					}
					if (status == expectedStatus) {
						Assert.assertTrue("Expected " + mustSeeNext + " but got " + seen, mustSeeNext == null);
					}
				}
			};
			Iterator<Message> messages = null;
			try {
				// run in new thread and propagate exceptions:
				try {
					FutureTask<Boolean> t = new FutureTask<Boolean>(poller, true);
					t.run();
					t.get();
				} catch (ExecutionException e) {
					if (e.getCause() instanceof AssertionError)
						throw (AssertionError)e.getCause();
					else if (e.getCause() instanceof RuntimeException)
						throw (RuntimeException)e.getCause();
					else
						throw e;
				} finally {
					messages = printMessages(accessor.getAll().iterator());
				}
				final Counter seq = new Counter(0);
				seq.get(); // p:identity
				assertMessage(next(messages), seq.get(), Message.Level.INFO, "px:progress-messages (1)",
				              msgs -> {
				                  seq.get(); // for-each iteration
				                  seq.get(); // px:java-step
				                  assertMessage(next(msgs), seq.get(), Message.Level.INFO, "px:java-step (1)");
				                  assertMessage(next(msgs), seq.get(), Message.Level.INFO, "px:java-step (2)");
				                  seq.get(); // for-each iteration
				                  seq.get(); // px:java-step
				                  assertMessage(next(msgs), seq.get(), Message.Level.INFO, "px:java-step (1)");
				                  assertMessage(next(msgs), seq.get(), Message.Level.INFO, "px:java-step (2)");
				                  Assert.assertFalse(msgs.hasNext()); });
				seq.get(); // p:wrap-sequence
				assertMessage(next(messages), seq.get(), Message.Level.INFO, "px:progress-messages (2)",
				              msgs -> {
				                  assertMessage(next(msgs), seq.get(), Message.Level.INFO, "px:foo (1)");
				                  assertMessage(next(msgs), seq.get(), Message.Level.INFO, "px:foo (2)");
				                  Assert.assertFalse(msgs.hasNext()); });
				assertMessage(next(messages), seq.get(), Message.Level.INFO, "px:progress-messages (3)");
				assertMessage(
					next(messages), seq.get(), Message.Level.INFO, "px:progress-messages (4)",
					submsgs -> {
						assertMessage(
							next(submsgs), seq.get(), Message.Level.INFO, "px:progress-messages (4a)",
							subsubmsgs -> {
								assertMessage(next(subsubmsgs), seq.get(), Message.Level.INFO, "same message");
								assertMessage(next(subsubmsgs), seq.get(), Message.Level.INFO, "same message");
								assertMessage(next(subsubmsgs), seq.get(), Message.Level.INFO, "same message");
								assertMessage(next(subsubmsgs), seq.get(), Message.Level.INFO, "other message");
								assertMessage(next(subsubmsgs), seq.get(), Message.Level.INFO, "same message");
								assertMessage(
									next(subsubmsgs), seq.get(), Message.Level.INFO,
									"same message (further repetitions of this message will be omitted)");
								Assert.assertFalse(subsubmsgs.hasNext()); });
						Assert.assertFalse(submsgs.hasNext()); });
				Assert.assertFalse(messages.hasNext());
				Iterator<ILoggingEvent> log = collectLog.get();
				Assert.assertFalse(log.hasNext());
			} catch (Throwable e) {
				// print remainging messages
				sink(messages);
				throw e;
			}
		} finally {
			logger.detachAppender(collectLog);
		}
	}
	
	Job newJob(String scriptId) {
		ScriptService<?> script = scriptRegistry.getScript(scriptId);
		Assert.assertNotNull("The " + scriptId + " script should exist", script);
		Job job = jobFactory.newJob(new BoundScript.Builder(script.load()).build())
		                    .withNiceName("nice")
		                    .build()
		                    .get();
		new Thread(job).start();
		return job;
	}
	
	static void waitForStatus(Job.Status status, Job job, long timeout) {
		new JobPoller(job, status, 500, timeout).run();
	}
	
	static <T> Stream<T> stream(T... array) {
		return Arrays.<T>stream(array);
	}

	static <T> Optional<T> next(Iterator<T> iterator) {
		if (iterator.hasNext())
			return Optional.<T>of(iterator.next());
		else
			return Optional.<T>absent();
	}
	
	static void sink(Iterator<?> iterator) {
		try {
			Iterators.getLast(iterator);
		} catch (NoSuchElementException e) {
		}
	}
	
	static <T> T assertPresent(String message, Optional<T> optional) {
		Assert.assertTrue(message, optional.isPresent());
		return optional.get();
	}

	static void assertMessage(Optional<? extends Message> message,
	                          int expectedSequence, Message.Level expectedLevel, String expectedText) {
		assertMessage(message, expectedSequence, expectedLevel, expectedText, null);
	}
	
	static void assertMessage(Optional<? extends Message> message,
	                          int expectedSequence, Message.Level expectedLevel, String expectedText,
	                          Consumer<Iterator<? extends Message>> assertChildMessages) {
		assertMessage(message, expectedSequence, expectedLevel, t -> expectedText.equals(t), assertChildMessages);
	}
	
	static void assertMessage(Optional<? extends Message> message,
	                          int expectedSequence, Message.Level expectedLevel, Predicate<? super String> expectedText) {
		assertMessage(message, expectedSequence, expectedLevel, expectedText, null);
	}
	
	static void assertMessage(Optional<? extends Message> message,
	                          int expectedSequence, Message.Level expectedLevel, Predicate<? super String> expectedText,
	                          Consumer<Iterator<? extends Message>> assertChildMessages) {
		Message m = assertPresent("message does not exist", message);
		Assert.assertEquals("message sequence number does not match", expectedSequence, ((ProgressMessage)m).getSequence());
		Assert.assertEquals("message level does not match", expectedLevel, m.getLevel());
		Assert.assertTrue("message text does not match", expectedText.apply(m.getText()));
		if (assertChildMessages != null) {
			Assert.assertTrue("message must be of type ProgressMessage", m instanceof ProgressMessage);
			assertChildMessages.accept(((ProgressMessage)m).iterator());
		} else if (m instanceof ProgressMessage) {
			Assert.assertTrue("message must not have children", !((ProgressMessage)m).iterator().hasNext());
		}
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
	
	static void printMessage(Message message, PrintStream out) {
		printMessage(message, out, "");
	}
	
	static Iterator<Message> printMessages(Iterator<Message> messages) {
		return Iterators.transform(
			messages,
			m -> {
				printMessage(m, System.out);
				return m; });
	}
	
	static void printMessage(Message message, PrintStream out, String indent) {
		out.print(indent + message.getSequence() + ": " + message.getLevel() + ": " + message.getText());
		if (message instanceof ProgressMessage) {
			ProgressMessage jm = (ProgressMessage)message;
			out.println(" [" + jm.getPortion() + "]");
			for (Message m : jm)
				printMessage(m, out, indent + "└─ ");
		} else
			out.println();
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
	
	// FIXME: can dependencies on modules-registry be eliminated?
	@Override
	public String[] testDependencies() {
		return new String[]{
			"com.google.guava:guava:?",
			"org.daisy.libs:com.xmlcalabash:?",
			"org.daisy.libs:saxon-he:?",
			"org.slf4j:slf4j-api:?",
			"org.daisy.pipeline:common-utils:?",
			"org.daisy.pipeline:framework-core:?",
			"org.daisy.pipeline:saxon-adapter:?",
			"org.daisy.pipeline:xproc-api:?",
			"org.daisy.pipeline:modules-registry:?",
			"org.apache.httpcomponents:httpclient-osgi:?",
			"org.apache.httpcomponents:httpcore-osgi:?",
			"org.daisy.libs:jing:?",
			"org.daisy.pipeline:logging-appender:?"
		};
	}
	
	@Override
	protected Properties systemProperties() {
		Properties p = new Properties();
		p.setProperty("org.daisy.pipeline.persistence", "false");
		return p;
	}
	
	@Override @Configuration
	public Option[] config() {
		return super.config();
	}

	@ProbeBuilder
	public TestProbeBuilder probeConfiguration(TestProbeBuilder probe) {
		probe.setHeader("Bundle-Name", "Test module");
		probe.setHeader("Service-Component", "OSGI-INF/script.xml,"
		                                   + "OSGI-INF/java-step.xml,"
		                                   + "OSGI-INF/java-function.xml");
		return probe;
	}
	
	static class Counter implements Supplier<Integer> {
		int val = 0;
		Counter(int initialValue) {
			val = initialValue;
		}
		public Integer get() {
			return val++;
		}
	};
	
	static class JobPoller implements Runnable {
		final Job job;
		final Job.Status expectedStatus;
		final long interval;
		final long timeout;
		JobPoller(Job job, Job.Status expectedStatus, long interval, long timeout) {
			this.job = job;
			this.expectedStatus = expectedStatus;
			this.interval = interval;
			this.timeout = timeout;
		}
		void performAction(Job.Status status) {}
		public void run() {
			long waited = 0L;
			while (true) {
				Job.Status status = job.getStatus();
				if (status == expectedStatus) {
					performAction(status);
					return;
				} else if (status == Job.Status.ERROR) {
					throw new RuntimeException("Job errored while waiting for another status");
				}
				try {
					performAction(status);
					Thread.sleep(interval);
					waited += interval;
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
				if (waited > timeout) {
					throw new RuntimeException("waitForStatus " + expectedStatus + " timed out (last status was " + status + ")");
				}
			}
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
}
