package org.daisy.pipeline.webservice;

import java.io.File;
import java.net.URI;

import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.job.JobIdFactory;
import org.daisy.pipeline.job.JobManager;
import org.daisy.pipeline.job.JobResult;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

// TODO: Auto-generated Javadoc
/**
 * The Class ResultResource.
 */
public class ResultResource extends ServerResource {
	
	/** The job. */
	private Job job;
	
	/* (non-Javadoc)
	 * @see org.restlet.resource.Resource#doInit()
	 */
	@Override  
    public void doInit() {  
		super.doInit();
		JobManager jobMan = ((PipelineWebService)this.getApplication()).getJobManager();
        String idParam = (String) getRequestAttributes().get("id");  
        JobId id = JobIdFactory.newIdFromString(idParam);
        job = jobMan.getJob(id); 
    }  
  
	/**
	 * Gets the resource.
	 *
	 * @return the resource
	 */
	@Get
    public Representation getResource() {  
    	if (job == null) {
    		setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			return null;
    	}
		
		if (!job.getStatus().equals(Job.Status.DONE)) {
    		setStatus(Status.CLIENT_ERROR_NOT_FOUND);
    		return null;
    	}
    	
		JobResult result = job.getResult();
		URI zip = result.getZip();
		// TODO check for errors instead of looking at null-ness of zip
		if (zip == null) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
    		return null;
		}
		FileRepresentation rep = new FileRepresentation(new File(zip), MediaType.APPLICATION_ZIP);
		setStatus(Status.SUCCESS_OK);
		return rep;
    }  
}
