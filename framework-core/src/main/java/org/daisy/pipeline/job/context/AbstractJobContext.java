package org.daisy.pipeline.job.context;

import java.net.URI;

import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcMonitor;
import org.daisy.common.xproc.XProcOutput;
import org.daisy.common.xproc.XProcResult;
import org.daisy.pipeline.event.EventBusProvider;
import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.job.JobMonitorFactory;
import org.daisy.pipeline.job.JobURIUtils;
import org.daisy.pipeline.job.ResultSet;
import org.daisy.pipeline.job.ResultSetFactory;
import org.daisy.pipeline.job.RuntimeConfigurable;
import org.daisy.pipeline.job.URIMapper;
import org.daisy.pipeline.job.ResultSet.Builder;
import org.daisy.pipeline.script.BoundXProcScript;
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
	private XProcInput input;

	/** The output. */
	private XProcOutput output;

	/**Script details*/
	private XProcScript script;

	private JobId id;
	/** bus */
	private EventBus bus;

	/** monitor */
	private XProcMonitor monitor;

	private URI logFile;

	private URIMapper mapper;  

	private ResultSet results;
		
	private String niceName;

	public AbstractJobContext(JobId id,String niceName,BoundXProcScript boundScript,URIMapper mapper){
		if(boundScript!=null){
			this.input=boundScript.getInput();
			this.script=boundScript.getScript();
			this.output=boundScript.getOutput();		
		}

		this.id=id;
		this.niceName=niceName;
		this.mapper=mapper;

		if(id!=null)
			this.logFile=JobURIUtils.getLogFile(id);
		else
			this.logFile=URI.create("");

		this.results=new ResultSet.Builder().build();
		
	}


	@Override
	public XProcInput getInputs() {
		return this.input;
	}

	@Override
	public XProcOutput getOutputs() {
		return this.output;
	}


	@Override
	public URI getLogFile() {
		return this.logFile;
	}

	protected void setLogFile(URI logFile) {
		this.logFile=logFile;
	}


	/**
	 * Gets the mapper for this instance.
	 *
	 * @return The mapper.
	 */
	public URIMapper getMapper() {
		return this.mapper;
	}

	/**
	 * Sets the mapper for this instance.
	 *
	 * @param mapper The mapper.
	 */
	protected void setMapper(URIMapper mapper) {
		this.mapper = mapper;
	}

	/**
	 * Sets the results for this instance.
	 *
	 * @param results The results.
	 */
	protected void setResults(ResultSet results) {
		this.results = results;
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

	/**
	 * Sets the output for this instance.
	 *
	 * @param output The output.
	 */
	protected void setOutput(XProcOutput output)
	{
		this.output = output;
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
	public ResultSet getResults() {
		return this.results;
	}

	@Override
	public void setEventBusProvider(EventBusProvider eventBusProvider) {
		this.bus=eventBusProvider.get();
	}

	@Override
	public void setMonitorFactory(JobMonitorFactory monitor) {
		this.monitor=monitor.newJobMonitor(this.id);
	}

	@Override
	public void writeResult(XProcResult result) {
		result.writeTo(this.output);
		this.results=ResultSetFactory.newResultSet(this,this.mapper);
				
	}

	public void cleanUp(){
		logger.info(String.format( "Deleting context for job %s" ,this.id));
		JobURIUtils.cleanJobBase(this.id);
		this.monitor.getMessageAccessor().delete();
	}


	@Override
	public String getName() {
		return niceName;
	}

	protected void setName(String name) {
		this.niceName=name;
	}

}