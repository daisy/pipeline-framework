package org.daisy.pipeline.persistence.jobs;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.daisy.common.base.Provider;
import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcPortInfo;
import org.daisy.common.xproc.XProcResult;
import org.daisy.pipeline.job.AbstractJobContext;
import org.daisy.pipeline.job.JobContextFactory;
import org.daisy.pipeline.job.JobIdFactory;
import org.daisy.pipeline.job.JobResult;
import org.daisy.pipeline.job.ResultSet;
import org.daisy.pipeline.job.RuntimeConfigurable;
import org.daisy.pipeline.script.BoundXProcScript;
import org.daisy.pipeline.script.ScriptRegistry;
import org.daisy.pipeline.script.XProcScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class persists job contexts.
 * The general idea is to write getters and setters (Access property)  when possible
 * to make proxification as transparent as possible.
 * Complex depenedencies of the context like ports, options, mapper
 * and results are wrapped in their own persistent objects so in this
 * case they're are persisted as fields. 
 * @author Javier Asensio Cubero capitan.cambio@gmail.com
 */
@Entity
@Table(name="job_contexts")
@Access(AccessType.FIELD)
public final class PersistentJobContext extends AbstractJobContext implements Serializable,RuntimeConfigurable{
	public static final long serialVersionUID=1L;
	private static final Logger logger = LoggerFactory.getLogger(PersistentJobContext.class);


	//embedded mapper
	@Embedded
	PersistentMapper pMapper;

	@OneToMany(cascade = CascadeType.ALL,fetch=FetchType.EAGER)
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

	@OneToMany(cascade = CascadeType.ALL,fetch=FetchType.EAGER)
	@MapsId("job_id")
	//@JoinColumn(name="job_id",referencedColumnName="job_id")
	List<PersistentPortResult> portResults= new ArrayList<PersistentPortResult>();

	@OneToMany(cascade = CascadeType.ALL,fetch=FetchType.EAGER)
	@MapsId("job_id")
	//@JoinColumn(name="job_id",referencedColumnName="job_id")
	List<PersistentOptionResult> optionResults= new ArrayList<PersistentOptionResult>();

	public PersistentJobContext(AbstractJobContext ctxt) {
		super(ctxt.getId(),ctxt.getName(),BoundXProcScript.from(ctxt.getScript(),ctxt.getInputs(),ctxt.getOutputs()),ctxt.getMapper());
		this.pMapper=new PersistentMapper(this.getMapper());
		this.setResults(ctxt.getResults());
		this.load();
	}

	/**
	 * Constructs a new instance.
	 */
	public PersistentJobContext() {
		super(null,"",null,null);
	}

	/**
	 * Maps complex objects to their Persistent representation
	 */
	private void load(){
		logger.debug("coping the objects to the model ");
		for( XProcPortInfo portName:this.getScript().getXProcPipelineInfo().getInputPorts()){
			PersistentInputPort anon=new PersistentInputPort(this.getId(),portName.getName());
			for (Provider<Source> src:this.getInputs().getInputs(portName.getName())){
				anon.addSource(new PersistentSource(src.provide().getSystemId()));
			}
			this.inputPorts.add(anon);
		}
		// options 
		for(QName option:this.getInputs().getOptions().keySet()){
			this.options.add(new PersistentOption(this.getId(),option,this.getInputs().getOptions().get(option)));
		}
		//parameters 
		for( String portName:this.getScript().getXProcPipelineInfo().getParameterPorts()){
			for (QName paramName :this.getInputs().getParameters(portName).keySet()){
				this.parameters.add(new PersistentParameter(this.getId(),portName,paramName,this.getInputs().getParameters(portName).get(paramName)));
			}
		}

		//results 
		//everything is inmutable but this
		this.updateResults();	
	}


	@PostLoad
	public void postLoad(){
		logger.debug("Post loading jobcontext");
		//we have all the model but we have to hidrate the actual objects
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
		this.setInput(builder.build());

		this.setMapper(this.pMapper.getMapper());

		ResultSet.Builder rBuilder=new ResultSet.Builder();

		for(PersistentPortResult pRes: this.portResults){
			rBuilder.addResult(pRes.getPortName(),pRes.getJobResult());
		}
		for(PersistentOptionResult pRes: this.optionResults){
			rBuilder.addResult(pRes.getOptionName(),pRes.getJobResult());
		}
		this.setResults(rBuilder.build());
		//so the context is configured once it leaves to the real world.
		if (ctxtFactory!=null)
			ctxtFactory.configure(this);
		
	}

	private void updateResults(){
		ResultSet rSet= this.getResults();
		if(this.portResults.size()==0)
			for(String port:rSet.getPorts()){
				for(JobResult res:rSet.getResults(port)){
					this.portResults.add(new PersistentPortResult(this.getId(),res,port));
				}
			}
		if(this.optionResults.size()==0)
			for(QName option:rSet.getOptions()){
				logger.debug(String.format(" Persisting job context with # %d result options",rSet.getResults(option).size()));
				for(JobResult res:rSet.getResults(option)){
					this.optionResults.add(new PersistentOptionResult(this.getId(),res,option));
				}

			}
	}

	/**
	 * @return the sId
	 */
	@Column(name="job_id")
	@Id
	@Access(AccessType.PROPERTY)
	@SuppressWarnings("unused") //used by jpa
	private String getStringId() {
		return this.getId().toString();
	}

	/**
	 * @param sId the sId to set
	 */
	@SuppressWarnings("unused") //used by jpa
	private void setStringId(String sId) {
		super.setId(JobIdFactory.newIdFromString(sId));
	}


	/**
	 * @return the logFile
	 */
	@SuppressWarnings("unused") //used by jpa
	@Column(name="log_file")
	@Access(AccessType.PROPERTY)
	private String getStringLogFile() {
		if(super.getLogFile()==null)
			return "";
		return super.getLogFile().toString();
	}

	/**
	 * @param logFile the logFile to set
	 */
	@SuppressWarnings("unused") //used by jpa
	private void setStringLogFile(String logFile) {
		super.setLogFile(URI.create(logFile));
	}

	/**
	 * Gets the script for this instance.
	 *
	 * @return The script.
	 */
	@Column(name="script_uri")
	@Access(AccessType.PROPERTY)
	@SuppressWarnings("unused") //used by jpa
	private String getScriptUri() {
		if(this.getScript()!=null){
			return this.getScript().getURI().toString();
		}else{
			throw new IllegalStateException("Script is null");
		}

	}

	@SuppressWarnings("unused") //used by jpa
	private void setScriptUri(String uri) {
		if(registry!=null){
			XProcScript xcript=registry.getScript(URI.create(uri)).load();
			logger.debug(String.format("load script %s",xcript));
			this.setScript(xcript);//getScriptService(URI.create(this.scriptUri)).getScript();
		}else{
			throw new IllegalStateException(
					String.format("Illegal state for recovering XProcScript: registry %s"
						,this.getScript(),registry));
		}

	}

	/**
	 * @return the sNiceName
	 */
	@Column(name="nice_name")
	@Access(AccessType.PROPERTY)
	@Override
	public String getName() {
		return super.getName();
	}

	/**
	 * @param sNiceName the sNiceName to set
	 */
	@Override
	public void setName(String Name) {
		super.setName(Name);
	}


	@Override
	public void writeResult(XProcResult result) {
		//build the result set
		super.writeResult(result);
		//and make sure that the new values get stored
		this.updateResults();
				
	}
	//Configuration for adding runtime inforamtion	
	@Transient
	static ScriptRegistry registry;
	public static void setScriptRegistry(ScriptRegistry sregistry){
		registry=sregistry;
	}
	@Transient
	static JobContextFactory ctxtFactory;
	public static void setJobContextFactory(JobContextFactory jobContextFactory){
		PersistentJobContext.ctxtFactory=jobContextFactory;
	}

		
}
