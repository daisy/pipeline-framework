package org.daisy.pipeline.job.impl;

import org.daisy.common.priority.Priority;
import org.daisy.common.xproc.XProcEngine;
import org.daisy.pipeline.job.AbstractJob;
import org.daisy.pipeline.job.AbstractJobContext;

public class VolatileJob extends AbstractJob {

	public VolatileJob(AbstractJobContext ctxt, Priority priority, XProcEngine xprocEngine) {
		super(ctxt, priority, xprocEngine);
	}
}
