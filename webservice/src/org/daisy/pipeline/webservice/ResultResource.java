package org.daisy.pipeline.webservice;

import org.daisy.pipeline.DaisyPipelineContext;
import org.daisy.pipeline.jobmanager.Job;
import org.daisy.pipeline.jobmanager.JobID;
import org.daisy.pipeline.jobmanager.JobStatus;
import org.daisy.pipeline.jobmanager.Result;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class ResultResource extends ServerResource {
	private Job job;
	
	@Override  
    public void doInit() {  
		super.doInit();
		DaisyPipelineContext context = ((WebApplication)this.getApplication()).getDaisyPipelineContext();
		String idParam = (String) getRequestAttributes().get("id");  
        JobID jobId = context.getJobManager().getIDFactory().fromString(idParam);
        job = context.getJobManager().getJob(jobId);
    }  
  
	// TODO: @Get("zip")
    @Get
    public String getResource() {  
    	// get the results from the framework
    	if (job.getStatus().getStatus() != JobStatus.Status.COMPLETED) {
    		setStatus(Status.CLIENT_ERROR_NOT_FOUND);
    		return "error";
    	}
    	
    	if (job != null) {
    		
    		// TODO 
    		// 1. will the results be zipped up already in the framework?
    		// 2. are we sending a FileRepresentation or a StreamRepresentation?
    		
    		// This is the result object, but it doesn't do anything yet
    		Result result = job.getStatus().getResult();
			
    		return "<result>Results not implemented yet. We apologize for the inconvenience.</result>";
		}
		else {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			return "error";
		}
    	
    }  
}
