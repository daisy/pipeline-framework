package org.daisy.pipeline.webservice;

import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.job.JobIdFactory;
import org.daisy.pipeline.job.JobManager;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;


public class JobResource extends ServerResource {
	private Job job;
	
	@Override  
    public void doInit() {  
		super.doInit();
		JobManager jobMan = ((PipelineWebService)this.getApplication()).getJobManager();
        String idParam = (String) getRequestAttributes().get("id");  
        JobId id = JobIdFactory.newIdFromString(idParam);
        job = jobMan.getJob(id);
    }  
  
    @Get("xml")
    public Representation getResource() {  
    	if (job == null) {
    		setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			return null;
    	}
	
    	String serverAddress = ((PipelineWebService)this.getApplication()).getServerAddress();
		setStatus(Status.SUCCESS_OK);
		DomRepresentation dom = new DomRepresentation(MediaType.APPLICATION_XML, XmlFormatter.jobToXml(job, serverAddress));
		return dom;
    }  
    
	@Delete
	public void deleteResource() {
		if (job == null) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
		}
		
		JobManager jobMan = ((PipelineWebService)this.getApplication()).getJobManager();
		if (jobMan.deleteJob(job.getId())) {
			setStatus(Status.SUCCESS_NO_CONTENT);
		}
		else {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
		}
	}   
    
}
