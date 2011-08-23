package org.daisy.pipeline.webservice;

import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.job.JobIdFactory;
import org.daisy.pipeline.job.JobManager;
import org.restlet.data.Status;
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
	
	@Get
	public String getResource() {
		if (job != null) {
			setStatus(Status.SUCCESS_OK);
			
			// TODO: return the actual log, not just the status
			return "<log>" + job.getStatus().name() + "</log>";
		}
		else {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			return "";
		}
		
	}
}
