package org.daisy.pipeline.job.context;

import java.net.URI;

import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcMonitor;
import org.daisy.common.xproc.XProcOutput;
import org.daisy.common.xproc.XProcResult;
import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.job.result.ResultSet;
import org.daisy.pipeline.script.XProcScript;

import com.google.common.eventbus.EventBus;

public interface JobContext{
	public XProcInput getInputs() ;
	public XProcOutput getOutputs();
	public URI getLogFile() ;
	public XProcMonitor getMonitor() ;
	public EventBus getEventBus() ;
	public XProcScript getScript(); 
	public JobId getId();
	public ResultSet getResults();
	public void writeResult(XProcResult result) ;
	public String getName();
}
