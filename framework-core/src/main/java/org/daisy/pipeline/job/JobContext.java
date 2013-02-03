package org.daisy.pipeline.job;

import java.net.URI;

import java.util.Set;

import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcMonitor;
import org.daisy.common.xproc.XProcOutput;
import org.daisy.common.xproc.XProcResult;

import org.daisy.pipeline.script.XProcScript;

import com.google.common.eventbus.EventBus;

public interface JobContext{
	public XProcInput getInputs() ;
	public XProcOutput getOutputs();
	void writeXProcResult(XProcResult result) ;
	public URI getLogFile() ;
	public XProcMonitor getMonitor() ;
	public EventBus getEventBus() ;
	public XProcScript getScript(); 
	public JobId getId();
	public ResultSet getResults();
}
