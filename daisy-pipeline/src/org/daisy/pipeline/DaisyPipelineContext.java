package org.daisy.pipeline;

import org.daisy.pipeline.jobmanager.JobManager;
import org.daisy.pipeline.modules.converter.ConverterRegistry;

public interface DaisyPipelineContext {
	public ConverterRegistry getConverterRegistry();
	public JobManager getJobManager();
}
