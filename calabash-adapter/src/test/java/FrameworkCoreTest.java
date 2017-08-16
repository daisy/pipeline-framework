import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import com.google.common.base.Function;
import com.google.common.base.Optional;
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
import org.daisy.pipeline.job.StatusMessage;

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
		CollectMessages collectMessages = new CollectMessages();
		EventBus bus = eventBusProvider.get();
		bus.register(collectMessages);
		try {
			OutputPortReader resultPort = new OutputPortReader();
			Job job = newJob("fail",
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
		} finally {
			bus.unregister(collectMessages);
		}
	}
	
	@Test
	public void testUncaughtXProcError() {
		CollectMessages collectMessages = new CollectMessages();
		EventBus bus = eventBusProvider.get();
		bus.register(collectMessages);
		try {
			Job job = newJob("xproc-error");
			String id = job.getId().toString();
			waitForStatus(Job.Status.ERROR, job, 1000);
			Iterator<Message> messages = collectMessages.get(id);
			int seq = 0;
			assertMessage(seq++, "FOO:foobar", next(messages));
			assertMessage(seq++, "com.xmlcalabash.core.XProcException: foobar", next(messages));
			Assert.assertFalse(messages.hasNext());
		} finally {
			bus.unregister(collectMessages);
		}
	}
	
	@Test
	public void testUncaughtXslTerminateError() {
		CollectMessages collectMessages = new CollectMessages();
		EventBus bus = eventBusProvider.get();
		bus.register(collectMessages);
		try {
			Job job = newJob("xslt-terminate-error");
			String id = job.getId().toString();
			waitForStatus(Job.Status.ERROR, job, 1000);
			Iterator<Message> messages = collectMessages.get(id);
			int seq = 0;
			assertMessage(seq++, "Runtime Error", next(messages));
			assertMessage(seq++, "XTMM9000:Processing terminated by xsl:message at line -1 in null", next(messages));
			assertMessage(seq++, "Processing terminated by xsl:message at line -1 in null", next(messages));
			assertMessage(seq++, "net.sf.saxon.s9api.SaxonApiException: Processing terminated by xsl:message at line -1 in null", next(messages));
			Assert.assertFalse(messages.hasNext());
		} finally {
			bus.unregister(collectMessages);
		}
	}
	
	@Test
	public void testUncaughtJavaError() {
		CollectMessages collectMessages = new CollectMessages();
		EventBus bus = eventBusProvider.get();
		bus.register(collectMessages);
		try {
			Job job = newJob("java-runtime-error");
			String id = job.getId().toString();
			waitForStatus(Job.Status.ERROR, job, 1000);
			Iterator<Message> messages = collectMessages.get(id);
			int seq = 0;
			assertMessage(seq++, "java.lang.RuntimeException: foobar", next(messages));
			Assert.assertFalse(messages.hasNext());
		} finally {
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
	
	static void assertMessage(int expectedSequence, String expectedText, Optional<Message> message) {
		Assert.assertTrue("message does not exist", message.isPresent());
		Message m = message.get();
		Assert.assertEquals("message sequence number does not match", expectedSequence, m.getSequence());
		Assert.assertEquals("message text does not match", expectedText, m.getText());
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
		                                   + "OSGI-INF/java-step.xml");
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
