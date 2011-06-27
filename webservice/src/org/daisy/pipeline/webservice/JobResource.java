package org.daisy.pipeline.webservice;

import org.daisy.pipeline.DaisyPipelineContext;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.daisy.pipeline.jobmanager.Job;


public class JobResource extends ServerResource {
	private Job job;
	
	@Override  
    public void doInit() {  
		super.doInit();
		DaisyPipelineContext context = ((WebApplication)this.getApplication()).getDaisyPipelineContext();
        String jobId = (String) getRequestAttributes().get("id");  
        job = Utilities.getJob(jobId, context);
    }  
  
    @Get("xml")
    public Representation getResource() {  
    	if (job != null) {
			setStatus(Status.SUCCESS_OK);
    		DomRepresentation dom = new DomRepresentation(MediaType.APPLICATION_XML, XmlFormatter.jobToXml(job));
    		return dom;
		}
		else {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			return null;
		}
    	
    }  
    
	@Delete
	public void deleteResource() {
		if (job != null) {
			DaisyPipelineContext context = ((WebApplication)this.getApplication()).getDaisyPipelineContext();
			if (context.getJobManager().deleteJob(job.getId())) {
				setStatus(Status.SUCCESS_NO_CONTENT);
			}
			else {
				setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			}
			
		}
		else {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
		}
	}   
    
}
