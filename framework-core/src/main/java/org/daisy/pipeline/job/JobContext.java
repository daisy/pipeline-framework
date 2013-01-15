package org.daisy.pipeline.job;

import java.net.URI;

import java.util.Set;

import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcMonitor;

import org.daisy.pipeline.script.XProcScript;

import com.google.common.eventbus.EventBus;

/** This class defines the common behaviour to jobs contexts, the context will mainly differ depending on the mode of 
 * the WS, local or remote. 
 * The subclasses of JobContext MUST define some fine grained behaviour regarding how the job interacts with the fs and 
 * input,output,option redirections.
 */
public abstract class JobContext {
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
		
	public JobContext(JobId id,XProcInput input,XProcScript script){
		this.input=input;
		this.id=id;
		this.script=script;
		
	}

	/**
	 * Constructs a new instance.
	 */
	public JobContext() {
	}

	public XProcInput getInputs(){return this.input;};
	public void writeXProcResult(){};
	public Set<URI> getFiles(){return null;};	 
	public URI getLogFile(){return this.logFile;}
	public URI getZip(){return null;}
	public URI toZip(URI ...files){ return null;}
	public XProcMonitor getMonitor() {return null;}
	public EventBus getEventBus() {return null;}

	/**
	 * Gets the script for this instance.
	 *
	 * @return The script.
	 */
	public XProcScript getScript() { return this.script; }

	/**
	 * Gets the id for this instance.
	 *
	 * @return The id.
	 */
	public JobId getId() { return this.id; }

}
