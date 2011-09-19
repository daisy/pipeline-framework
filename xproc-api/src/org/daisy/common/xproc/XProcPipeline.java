package org.daisy.common.xproc;

/**
 * The Interface XProcPipeline gives access to the pipeline info and allows to run a pipeline.
 */
public interface XProcPipeline {
	
	/**
	 * Gets the pipeline info object associated to this pipeline.
	 *
	 * @return the info
	 */
	XProcPipelineInfo getInfo();
	
	/**
	 * Runs the pipline.
	 *
	 * @param data the data
	 * @return the x proc result
	 */
	XProcResult run(XProcInput data);

}
