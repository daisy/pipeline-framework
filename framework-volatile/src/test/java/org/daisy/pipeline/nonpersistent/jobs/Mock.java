package org.daisy.pipeline.nonpersistent.jobs;

import org.daisy.pipeline.clients.Client.Role;
import org.daisy.pipeline.job.AbstractJobContext;
import org.daisy.pipeline.job.JobContext;
import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.job.JobIdFactory;
import org.daisy.pipeline.job.URIMapper;
import org.daisy.pipeline.job.priority.Priority;
import org.daisy.pipeline.nonpersistent.webservice.VolatileClient;
import org.daisy.pipeline.script.BoundXProcScript;

public class Mock {

	static class MockedJobContext extends AbstractJobContext {

		public MockedJobContext(JobId id, String niceName,
				BoundXProcScript boundScript, URIMapper mapper) {
			super(new VolatileClient("","",Role.ADMIN,"",Priority.MEDIUM),id, niceName, boundScript, mapper);
			// TODO Auto-generated constructor stub
		}
	}


	public static JobContext newJobContext(){
		return new MockedJobContext(JobIdFactory.newId(),null,null,null);
	}
	
}
