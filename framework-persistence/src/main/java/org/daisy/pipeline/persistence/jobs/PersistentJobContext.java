package org.daisy.pipeline.persistence.jobs;

import java.io.Serializable;

import java.net.URI;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import javax.xml.namespace.QName;

import javax.xml.transform.Source;

import org.daisy.common.base.Provider;

import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcPortInfo;
import org.daisy.pipeline.event.EventBusProvider;
import org.daisy.pipeline.job.AbstractJobContext;
import org.daisy.pipeline.job.JobContext;
import org.daisy.pipeline.job.JobContextFactory;
import org.daisy.pipeline.job.JobFactory;
import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.job.JobMonitorFactory;

import javax.persistence.Entity;

import org.daisy.pipeline.job.JobIdFactory;
import org.daisy.pipeline.job.RuntimeConfigurable;

import org.daisy.pipeline.script.XProcScript;

@Entity
@Table(name="job_contexts")
public class PersistentJobContext extends AbstractJobContext implements Serializable,RuntimeConfigurable{
	public static final long serialVersionUID=1L;
	@Id
	@Column(name="job_id")
	String sId;
	
	String logFile;

	String scriptUri;

	//this is virtual, the xproc input is not stored as such
	@Transient
	XProcInput input;

	@OneToMany(cascade = CascadeType.ALL,fetch=FetchType.EAGER)
	//@JoinColumn(name="job_id",referencedColumnName="job_id")
	@MapsId("job_id")
	List<PersistentInputPort> inputPorts= new ArrayList<PersistentInputPort>();

	@OneToMany(cascade = CascadeType.ALL,fetch=FetchType.EAGER)
	//@JoinColumn(name="job_id",referencedColumnName="job_id")
	@MapsId("job_id")
	List<PersistentOption> options= new ArrayList<PersistentOption>();

	@OneToMany(cascade = CascadeType.ALL,fetch=FetchType.EAGER)
	@MapsId("job_id")
	//@JoinColumn(name="job_id",referencedColumnName="job_id")
	List<PersistentParameter> parameters= new ArrayList<PersistentParameter>();

	public PersistentJobContext(JobContext ctxt) {
		super(ctxt.getId(),ctxt.getScript(),ctxt.getInputs(),ctxt.getOutputs());
		this.sId=ctxt.getId().toString();

		if (ctxt.getLogFile()==null)
			this.logFile="";
		else
			this.logFile=ctxt.getLogFile().toString();

		this.scriptUri=ctxt.getScript().getURI().toString();
		this.input=ctxt.getInputs();
	}

	/**
	 * Constructs a new instance.
	 */
	public PersistentJobContext() {
		super(null,null,null,null);
	}

	@PrePersist
	@PreUpdate
	public void buildPersistentInputs(){
		if(this.getScript()==null){
			this.setScript(ScriptRegistryHolder.load(URI.create(this.scriptUri)));//getScriptService(URI.create(this.scriptUri)).getScript();
		}
		//input ports	
		for( XProcPortInfo portName:this.getScript().getXProcPipelineInfo().getInputPorts()){
			PersistentInputPort anon=new PersistentInputPort(this.getId(),portName.getName());
			for (Provider<Source> src:this.input.getInputs(portName.getName())){
				anon.addSource(new PersistentSource(src.provide().getSystemId()));
			}
			this.inputPorts.add(anon);
		}
		// options 
		for(QName option:this.input.getOptions().keySet()){
			this.options.add(new PersistentOption(this.getId(),option,this.input.getOptions().get(option)));
		}
		//parameters 
		for( String portName:this.getScript().getXProcPipelineInfo().getParameterPorts()){
			for (QName paramName :this.input.getParameters(portName).keySet()){
				this.parameters.add(new PersistentParameter(this.getId(),portName,paramName,this.input.getParameters(portName).get(paramName)));
			}
		}
	}

	@PostLoad
	public void loadPersistentInputs(){
		XProcInput.Builder builder= new XProcInput.Builder();	
		for ( PersistentInputPort input:this.inputPorts){
			for (PersistentSource src:input.getSources()){
				builder.withInput(input.getName(),src);
			}
		}
		for (PersistentOption option:this.options){
			builder.withOption(option.getName(),option.getValue());
		}
		for(PersistentParameter param:this.parameters){
			builder.withParameter(param.getPort(),param.getName(),param.getValue());
		}
		this.input=builder.build();
		//FIXME!!: this should not be like this 
		//the service should be set to an element of this bundle and then accessed
		JobContextFactory.getInstance().configure(this);
	}


// Getters and setters

	public void setScript(XProcScript script){
		this.script=script;
	}

	/**
	 * Gets the id for this instance.
	 *
	 * @return The id.
	 */
	public JobId getId() {
		return JobIdFactory.newIdFromString(this.sId);
	}

	/**
	 * Sets the id for this instance.
	 *
	 * @param id The id.
	 */
	public void setId(JobId id) {
		this.sId =id.toString(); 
	}

	/**
	 * Gets the logFile for this instance.
	 *
	 * @return The logFile.
	 */
	public URI getLogFile() {
		return URI.create(this.logFile);
	}

	/**
	 * Sets the logFile for this instance.
	 *
	 * @param logFile The logFile.
	 */
	public void setLogFile(URI logFile) {
		this.logFile = logFile.toString();
	}

	/**
	 * Gets the script for this instance.
	 *
	 * @return The script.
	 */
	public URI getScriptUri() {
		return URI.create(this.scriptUri);
	}

	/**
	 * Sets the script for this instance.
	 *
	 * @param script The script.
	 */
	public void setScriptUri(URI script) {
		this.scriptUri = script.toString();
	}

	/**
	 * Gets the input for this instance.
	 *
	 * @return The input.
	 */
	public XProcInput getInput() {
		return this.input;
	}

	/**
	 * Sets the input for this instance.
	 *
	 * @param input The input.
	 */
	public void setInput(XProcInput input) {
		this.input = input;
	}

	@Override
	public void setEventBusProvider(EventBusProvider eventBusProvider) {
		this.bus=eventBusProvider.get();	
	}

	@Override
	public void setMonitorFactory(JobMonitorFactory factory) {
		this.monitor=factory.newJobMonitor(this.getId());	
	}

	@Override
	public void writeXProcResult() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<URI> getFiles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URI getZip() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URI toZip(URI... files) {
		// TODO Auto-generated method stub
		return null;
	}
		
}
