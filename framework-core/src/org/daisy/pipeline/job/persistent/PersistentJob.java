package org.daisy.pipeline.job.persistent;

import java.net.URI;

import org.daisy.common.base.Provider;
import org.daisy.common.xproc.XProcInput;
import org.daisy.pipeline.job.IOBridge;
import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.script.XProcScript;


public class PersistentJob {


	private static final long serialVersionUID = -3323221613585623709L;

	URI mScript;
	protected PersistentJob(JobId id, XProcScript script, XProcInput input,
			IOBridge ioBridge) {
		//super(id, script, input, ioBridge);

	}

	public Provider<Job> getProvider(){
		return null;
	}

}
