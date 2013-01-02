package org.daisy.pipeline.job;

import java.net.URI;

import java.util.Set;

import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcMonitor;

/** This class defines the common behaviour to jobs contexts, the context will mainly differ depending on the mode of 
 * the WS, local or remote. 
 * The subclasses of JobContext MUST define some fine grained behaviour regarding how the job interacts with the fs and 
 * input,output,option redirections.
 */
public abstract class JobContext {
	/** The input. */
	private final XProcInput input;
	/** monitor */
	private final XProcMonitor monitor;
	
	private final JobId id;
	
	public JobContext(JobId id,XProcInput input){
		this.input=input;
		this.monitor=null;
		this.id=id;
	}
	public XProcInput getInputs(){return null;};
	public void writeXProcResult(){};
	public Set<URI> getFiles(){return null;};	 
	public URI getLogFile(){return null;}
	public URI getZip(){return null;}
	public URI toZip(URI ...files){ return null;}
	public XProcMonitor getMonitor() {return null;}

	/**
	 * Gets the id for this instance.
	 *
	 * @return The id.
	 */
	public JobId getId() {
		return this.id;
	}

}
