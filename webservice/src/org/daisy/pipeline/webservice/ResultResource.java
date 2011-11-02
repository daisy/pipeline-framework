package org.daisy.pipeline.webservice;

import java.io.File;
import java.net.URI;

import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.job.JobIdFactory;
import org.daisy.pipeline.job.JobManager;
import org.daisy.pipeline.job.JobResult;
import org.restlet.data.Disposition;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

public class ResultResource extends AuthenticatedResource {
	private Job job;
	
	@Override  
    public void doInit() {  
		super.doInit();
		if (!isAuthenticated()) return;
		JobManager jobMan = ((PipelineWebService)this.getApplication()).getJobManager();
        String idParam = (String) getRequestAttributes().get("id");  
        JobId id = JobIdFactory.newIdFromString(idParam);
        job = jobMan.getJob(id); 
    }  
  
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
		//FileRepresentation rep = new FileRepresentation(new File(zip), MediaType.APPLICATION_ZIP);
		//setStatus(Status.SUCCESS_OK);
		//return rep;
		
		File zipFile = new File(zip);
		Representation rep = new FileRepresentation(zipFile,MediaType.APPLICATION_ZIP);
		 Disposition disposition = new Disposition();
		 disposition.setFilename(job.getId().toString() + ".zip");
		 disposition.setType(Disposition.TYPE_ATTACHMENT);
		 rep.setDisposition(disposition);
		 return rep;
    }  
}
