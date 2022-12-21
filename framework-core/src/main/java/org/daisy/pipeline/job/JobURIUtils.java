package org.daisy.pipeline.job;

import java.io.File;

public class JobURIUtils {

	public static File getLogFile(String jobId) {
		return org.daisy.pipeline.job.impl.JobURIUtils.getLogFile(jobId);
	}
}
