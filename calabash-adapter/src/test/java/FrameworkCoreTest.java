import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

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
import org.daisy.pipeline.script.XProcScript;
import org.daisy.pipeline.webserviceutils.storage.WebserviceStorage;

import org.junit.Assert;
import org.junit.Test;

import org.ops4j.pax.exam.Configuration;
import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.composite;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import org.ops4j.pax.exam.Option;
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
	public void testFail() {
		Object eventBusListener = new Object() {
			@Subscribe
			public void handleMessage(Message msg) {
				System.out.println(msg.getJobId() + ": " + msg.getText());
			}
			@Subscribe
			public void handleStatus(StatusMessage message) {
			}
		};
		EventBus bus = eventBusProvider.get();
		bus.register(eventBusListener);
		try {
			Client client = webserviceStorage.getClientStorage().defaultClient();
			JobManager jobManager = jobManagerFactory.createFor(client);
			XProcScript script = scriptRegistry.getScript("fail").load();
			Job job = jobManager.newJob(BoundXProcScript.from(script,
			                                                  new XProcInput.Builder().build(),
			                                                  new XProcOutput.Builder().build()))
			                    .isMapping(true)
			                    .withNiceName("nice")
			                    .build().get();
			waitForStatus(Job.Status.FAIL, job, 1000);
		} finally {
			bus.unregister(eventBusListener);
		}
	}
	
	@Test
	public void testUncaughtError() {
		final Map<String,List<Message>> messages = new HashMap<String,List<Message>>();
		Object eventBusListener = new Object() {
			@Subscribe
			public void handleMessage(Message msg) {
				String jobid = msg.getJobId();
				List<Message> list = messages.get(jobid);
				if (list == null) {
					list = new ArrayList<Message>();
					messages.put(jobid, list);
				}
				list.add(msg);
			}
			@Subscribe
			public void handleStatus(StatusMessage message) {
			}
		};
		EventBus bus = eventBusProvider.get();
		bus.register(eventBusListener);
		try {
			Client client = webserviceStorage.getClientStorage().defaultClient();
			JobManager jobManager = jobManagerFactory.createFor(client);
			XProcScript script = scriptRegistry.getScript("error").load();
			Job job = jobManager.newJob(BoundXProcScript.from(script,
			                                                  new XProcInput.Builder().build(),
			                                                  new XProcOutput.Builder().build()))
			                    .isMapping(true)
			                    .withNiceName("nice")
			                    .build().get();
			String id = job.getId().toString();
			waitForStatus(Job.Status.ERROR, job, 1000);
			Assert.assertTrue(messages.containsKey(id));
			Iterator<Message> i = messages.get(id).iterator();
			Assert.assertTrue(i.hasNext());
			Assert.assertEquals("Runtime Error", i.next().getText());
			Assert.assertTrue(i.hasNext());
			Assert.assertEquals("XTMM9000:Processing terminated by xsl:message at line -1 in null", i.next().getText());
			Assert.assertTrue(i.hasNext());
			Assert.assertEquals("Processing terminated by xsl:message at line -1 in null", i.next().getText());
			Assert.assertTrue(i.hasNext());
			Assert.assertEquals("net.sf.saxon.s9api.SaxonApiException: Processing terminated by xsl:message at line -1 in null",
			                    i.next().getText());
			Assert.assertFalse(i.hasNext());
		} finally {
			bus.unregister(eventBusListener);
		}
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
			bundle("reference:" + new File(PathUtils.getBaseDir(), "target/test-classes/module/").toURI()),
			systemProperty("org.daisy.pipeline.iobase").value(new File(PIPELINE_DATA, "jobs").getAbsolutePath()));
	}
}
