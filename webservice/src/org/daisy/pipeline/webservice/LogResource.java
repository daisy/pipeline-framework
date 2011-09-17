package org.daisy.pipeline.webservice;

import java.io.File;
import java.net.URI;

import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.job.JobIdFactory;
import org.daisy.pipeline.job.JobManager;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;


public class LogResource extends ServerResource {
	private Job job;

	@Override  
    public void doInit() {  
		super.doInit();
		JobManager jobMan = ((PipelineWebService)this.getApplication()).getJobManager();
        String idParam = (String) getRequestAttributes().get("id");  
        JobId id = JobIdFactory.newIdFromString(idParam);
        job = jobMan.getJob(id);
    }
	
	/*
	 * example output: daisy-pipeline/webservice/docs/sampleXml/log.xml
	 */
	@Get
	public Representation getResource() {
		if (job == null) {
    		setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			return null;
    	}
    	setStatus(Status.SUCCESS_OK);
    	
    	FileRepresentation logfile;
    	URI logfileUri = job.getResult().getLogFile();
    	if (logfileUri != null) {
    		logfile = new FileRepresentation(new File(job.getResult().getLogFile()), MediaType.TEXT_PLAIN);
    		return logfile;
    	}
    	else return null;
	}
}
