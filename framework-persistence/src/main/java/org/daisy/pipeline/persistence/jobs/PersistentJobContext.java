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
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcResult;
import org.daisy.pipeline.clients.Client;
import org.daisy.pipeline.job.AbstractJobContext;
import org.daisy.pipeline.job.JobContextFactory;
import org.daisy.pipeline.job.JobIdFactory;
import org.daisy.pipeline.job.ResultSet;
import org.daisy.pipeline.job.RuntimeConfigurable;
import org.daisy.pipeline.persistence.webservice.PersistentClient;
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
	private PersistentMapper pMapper;

        private PersistentClient pClient;

	@OneToMany(cascade = CascadeType.ALL,fetch=FetchType.EAGER)
	@MapsId("job_id")
	private List<PersistentInputPort> inputPorts= new ArrayList<PersistentInputPort>();

	@OneToMany(cascade = CascadeType.ALL,fetch=FetchType.EAGER)
	//@JoinColumn(name="job_id",referencedColumnName="job_id")
	@MapsId("job_id")
	private List<PersistentOption> options= new ArrayList<PersistentOption>();

	@OneToMany(cascade = CascadeType.ALL,fetch=FetchType.EAGER)
	@MapsId("job_id")
	//@JoinColumn(name="job_id",referencedColumnName="job_id")
	private List<PersistentParameter> parameters= new ArrayList<PersistentParameter>();

	@OneToMany(cascade = CascadeType.ALL,fetch=FetchType.EAGER)
	@MapsId("job_id")
	//@JoinColumn(name="job_id",referencedColumnName="job_id")
	private List<PersistentPortResult> portResults= new ArrayList<PersistentPortResult>();

	@OneToMany(cascade = CascadeType.ALL,fetch=FetchType.EAGER)
	@MapsId("job_id")
	//@JoinColumn(name="job_id",referencedColumnName="job_id")
	private List<PersistentOptionResult> optionResults= new ArrayList<PersistentOptionResult>();

	public PersistentJobContext(AbstractJobContext ctxt) {
		super(ctxt.getClient(),ctxt.getId(),ctxt.getName(),BoundXProcScript.from(ctxt.getScript(),ctxt.getInputs(),ctxt.getOutputs()),ctxt.getMapper());
		this.setResults(ctxt.getResults());
		this.generateResults=ctxt.isGeneratingResults();
		this.load();
	}

	/**
	 * Constructs a new instance.
	 */
	private PersistentJobContext() {
		super(null,null,"",null,null);
	}

	/**
	 * Maps complex objects to their Persistent representation
	 */
	private void load(){
		logger.debug("coping the objects to the model ");
		this.pMapper=new PersistentMapper(this.getMapper());
		this.inputPorts=ContextHydrator.dehydrateInputPorts(this);
		this.options=ContextHydrator.dehydrateOptions(this);
		this.parameters=ContextHydrator.dehydrateParameters(this);
                this.pClient=(PersistentClient)this.getClient();
		//everything is inmutable but this
		this.updateResults();	
	}


	/**
	 * Although we could delegate the actual hydration
	 * to setters (i.e. getInput) the performance would be affected.
	 * Therefore we prefer doing hydration on the PostLoad event.
	 */
	@PostLoad
	@SuppressWarnings("unused")//jpa only
	private void postLoad(){
		logger.debug("Post loading jobcontext");
		//we have all the model but we have to hidrate the actual objects
		XProcInput.Builder builder=new XProcInput.Builder();
		ContextHydrator.hydrateInputPorts(builder,inputPorts);
		ContextHydrator.hydrateOptions(builder,options);
		ContextHydrator.hydrateParams(builder,parameters);
		this.setInput(builder.build());

		this.setMapper(this.pMapper.getMapper());
                this.setClient(this.pClient);

		ResultSet.Builder rBuilder=new ResultSet.Builder();
		ContextHydrator.hydrateResultPorts(rBuilder,portResults);
		ContextHydrator.hydrateResultOptions(rBuilder,optionResults);
		this.setResults(rBuilder.build());

		//so the context is configured once it leaves to the real world.
		if (ctxtFactory!=null)
			ctxtFactory.configure(this);
		
	}

	private void updateResults(){
		ResultSet rSet= this.getResults();
		if(this.portResults.size()==0)
			this.portResults=ContextHydrator.dehydratePortResults(this);
		if(this.optionResults.size()==0)
			this.optionResults=ContextHydrator.dehydrateOptionResults(this);
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

	@Column(name= "generate_results")
	@Access(AccessType.PROPERTY)
	public boolean getGenerateResults(){
		return this.generateResults;
	}
	public void setGenerateResults(boolean generate){
		this.generateResults=generate;
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
