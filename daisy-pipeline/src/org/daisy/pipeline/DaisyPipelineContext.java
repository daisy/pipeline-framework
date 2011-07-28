package org.daisy.pipeline;

import java.net.URI;

import org.daisy.pipeline.jobmanager.JobManager;
import org.daisy.pipeline.modules.converter.ConverterRegistry;
import org.daisy.pipeline.modules.converter.Executor;
import org.daisy.pipeline.modules.converter.XProcRunnable;

public interface DaisyPipelineContext {
	public static final String MODE_PROPERTY="org.daisy.pipeline.mode";
	public ConverterRegistry getConverterRegistry();
	public JobManager getJobManager();
	public XProcRunnable newXprocRunnalble();
	public Executor getExecutor();
}
