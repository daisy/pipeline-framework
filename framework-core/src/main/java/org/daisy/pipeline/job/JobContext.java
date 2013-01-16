package org.daisy.pipeline.job;


import java.net.URI;

import java.util.Set;

import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcMonitor;

import org.daisy.pipeline.script.XProcScript;

import com.google.common.eventbus.EventBus;

public interface JobContext{
	public XProcInput getInputs() ;
	public void writeXProcResult() ;
	public Set<URI> getFiles() ;
	public URI getLogFile() ;
	public URI getZip() ;
	public URI toZip(URI ...files) ;
	public XProcMonitor getMonitor() ;
	public EventBus getEventBus() ;
	public XProcScript getScript(); 
	public JobId getId();
}
