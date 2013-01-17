package org.daisy.pipeline.job;

import java.net.URI;


import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcMonitor;

import org.daisy.pipeline.script.XProcScript;

import com.google.common.eventbus.EventBus;

/** This class defines the common behaviour to jobs contexts, the context will mainly differ depending on the mode of 
 * the WS, local or remote. 
 * The subclasses of JobContext MUST define some fine grained behaviour regarding how the job interacts with the fs and 
 * input,output,option redirections.
 */
public abstract class AbstractJobContext implements JobContext{
	/** The input. */
	protected XProcInput input;
	/**Script details*/
	protected XProcScript script;

	protected JobId id;
	/** bus */
	protected EventBus bus;

	/** monitor */
	protected XProcMonitor monitor;

	protected URI logFile;
		
	protected URITranslator translator; 
	public AbstractJobContext(JobId id,XProcInput input,XProcScript script){
		this.input=input;
		this.id=id;
		this.script=script;
		
	}

	/**
	 * Constructs a new instance.
	 */
	public AbstractJobContext() {
	}

	@Override
	public XProcInput getInputs() {
		return this.input;
	}

	@Override
	public URI getLogFile() {
		return this.logFile;
	}

	@Override
	public XProcMonitor getMonitor() {
		return this.monitor;
	}

	@Override
	public EventBus getEventBus() {
		return this.bus;
	}

	@Override
	public XProcScript getScript() {
		return this.script;
	}

	@Override
	public JobId getId() {
		return this.id;
	}

}
