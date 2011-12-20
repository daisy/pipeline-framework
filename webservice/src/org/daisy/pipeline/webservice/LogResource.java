package org.daisy.pipeline.webservice;

import java.io.File;
import java.net.URI;

import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.job.JobIdFactory;
import org.daisy.pipeline.job.JobManager;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

// TODO: Auto-generated Javadoc
/**
 * The Class LogResource.
 */

public class LogResource extends AuthenticatedResource {
	/** The job. */
	private Job job;

	/* (non-Javadoc)
	 * @see org.restlet.resource.Resource#doInit()
	 */
	@Override  
    public void doInit() {  
		super.doInit();
		if (!isAuthenticated()) return;
		
		JobManager jobMan = ((PipelineWebService)this.getApplication()).getJobManager();
        String idParam = (String) getRequestAttributes().get("id");  
        JobId id = JobIdFactory.newIdFromString(idParam);
        job = jobMan.getJob(id);
    }
	
	/*
	 * example output: daisy-pipeline/webservice/docs/sampleXml/log.xml
	 */
	/**
	 * Gets the resource.
	 *
	 * @return the resource
	 */
	@Get
	public Representation getResource() {
		if (!isAuthenticated()) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return null;
		}
	
		if (job == null) {
    		setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			return null;
    	}
		
		/*if (!job.getStatus().equals(Job.Status.DONE)) {
    		setStatus(Status.CLIENT_ERROR_NOT_FOUND);
    		return null;
    	}*/
    	
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
