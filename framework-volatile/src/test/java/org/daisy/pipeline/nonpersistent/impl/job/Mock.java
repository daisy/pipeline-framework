package org.daisy.pipeline.nonpersistent.impl.job;

import org.daisy.common.messaging.MessageAccessor;
import org.daisy.pipeline.clients.Client;
import org.daisy.pipeline.job.AbstractJobContext;
import org.daisy.pipeline.job.JobBatchId;
import org.daisy.pipeline.job.JobIdFactory;
import org.daisy.pipeline.job.JobMonitor;

import com.google.common.eventbus.EventBus;

public class Mock {

	static class MockedJobContext extends AbstractJobContext {

		public MockedJobContext(Client client) {
			this(client, null);
		}

		public MockedJobContext(Client client, JobBatchId batchId) {
			super();
			this.client = client;
			this.id = JobIdFactory.newId();
			this.batchId = batchId;
			this.niceName = "";
			EventBus bus = new EventBus();
			this.monitor = new JobMonitor() {
					@Override
					public MessageAccessor getMessageAccessor() {
						return null;
					}
					@Override
					public EventBus getEventBus() {
						return bus;
					}
				};
		}
	}
}
