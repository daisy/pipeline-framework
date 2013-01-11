package org.daisy.pipeline.persistence.jobs;

import org.daisy.common.xproc.XProcInput;
import org.daisy.pipeline.job.JobContext;
import org.daisy.pipeline.job.JobId;
import javax.persistence.Entity;

@Entity
class PersistentJobContext extends JobContext{

	public PersistentJobContext(JobContext ctxt) {
		super(ctxt.getId(), ctxt.getInputs(),ctxt.getScript());
	}
		
}
