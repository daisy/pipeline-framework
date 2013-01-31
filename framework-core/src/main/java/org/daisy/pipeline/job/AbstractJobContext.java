package org.daisy.pipeline.job;

import java.net.URI;




import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcMonitor;

import org.daisy.pipeline.event.EventBusProvider;

import org.daisy.pipeline.script.XProcScript;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

/** This class defines the common behaviour to jobs contexts, the context will mainly differ depending on the mode of 
 * the WS, local or remote. 
 * The subclasses of JobContext MUST define some fine grained behaviour regarding how the job interacts with the fs and 
 * input,output,option redirections.
 */
public abstract class AbstractJobContext implements JobContext,RuntimeConfigurable{
	private static final Logger logger = LoggerFactory.getLogger(AbstractJobContext.class);
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

	/**
	 * Sets the id for this instance.
	 *
	 * @param id The id.
	 */
	protected void setId(JobId id) {
		this.id = id;
	}

	@Override
	public XProcMonitor getMonitor() {
		return this.monitor;
	}

	@Override
	public EventBus getEventBus() {
		return this.bus;
	}

	/**
	 * Sets the input for this instance.
	 *
	 * @param input The input.
	 */
	protected void setInput(XProcInput input) {
		this.input = input;
	}

	@Override
	public XProcScript getScript() {
		return this.script;
	}

	/**
	 * Sets the script for this instance.
	 *
	 * @param script The script.
	 */
	protected void setScript(XProcScript script) {
		this.script = script;
	}

	@Override
	public JobId getId() {
		return this.id;
	}

	@Override
	public void setEventBusProvider(EventBusProvider eventBusProvider) {
		this.bus=eventBusProvider.get();
	}

	@Override
	public void setMonitorFactory(JobMonitorFactory monitor) {
		this.monitor=monitor.newJobMonitor(this.id);
		logger.debug(String.format("New monitor set to %s for %s",this.monitor,this));
	}

}
