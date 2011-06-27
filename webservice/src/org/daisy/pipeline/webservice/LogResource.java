package org.daisy.pipeline.webservice;

import org.daisy.pipeline.DaisyPipelineContext;
import org.daisy.pipeline.jobmanager.Job;
import org.daisy.pipeline.jobmanager.JobID;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;


public class LogResource extends ServerResource {
	private Job job;

	@Override  
    public void doInit() {  
		super.doInit();
		DaisyPipelineContext context = ((WebApplication)this.getApplication()).getDaisyPipelineContext();
        String idParam = (String) getRequestAttributes().get("id");  
        JobID jobId = context.getJobManager().getIDFactory().fromString(idParam);
        job = context.getJobManager().getJob(jobId);
    }
	
	@Get
	public String getResource() {
		if (job != null) {
			setStatus(Status.SUCCESS_OK);
			// TODO: return the actual log, not the path to the log file 
			return job.getStatus().getLog().toString();
		}
		else {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			return "";
		}
		
	}
}
