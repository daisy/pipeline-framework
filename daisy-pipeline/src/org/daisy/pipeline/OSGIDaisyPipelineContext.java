package org.daisy.pipeline;

import java.net.URI;

import org.daisy.pipeline.jobmanager.JobManager;
import org.daisy.pipeline.modules.converter.ConverterRegistry;
import org.daisy.pipeline.modules.converter.Executor;
import org.daisy.pipeline.modules.converter.XProcRunnable;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OSGIDaisyPipelineContext implements DaisyPipelineContext{
	Logger mLogger = LoggerFactory.getLogger(this.getClass().getName());
	ConverterRegistry mConverterRegistry;
	JobManager mJobManager;
	private Executor mExecutor;
	public void init(BundleContext context) {
		mLogger.debug("OSGIDaisyContext service up");
	}	
	
	public void setConverterRegistry(ConverterRegistry converterRegistry){
		mConverterRegistry=converterRegistry;
	}
	
	public ConverterRegistry getConverterRegistry() {
		mLogger.debug("setting converter registry");
		return mConverterRegistry;
	}

	public JobManager getJobManager() {
		return mJobManager;
	}

	public void setJobManager(JobManager jobManager) {
		mLogger.debug("setting job manager");
		mJobManager = jobManager;
	}

	@Override
	public XProcRunnable newXprocRunnalble() {
		return new XProcRunnable();
	}

	public void setExecutor(Executor executor){
		mExecutor=executor;
	}
	@Override
	public Executor getExecutor() {

		return mExecutor;
	}

	

}
