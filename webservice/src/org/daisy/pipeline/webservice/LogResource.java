package org.daisy.pipeline.webservice;

import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.job.JobIdFactory;
import org.daisy.pipeline.job.JobManager;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
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
	@Get("xml")
	public Representation getResource() {
		if (job == null) {
    		setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			return null;
    	}
    	setStatus(Status.SUCCESS_OK);
    	String serverAddress = ((PipelineWebService)this.getApplication()).getServerAddress();
		DomRepresentation dom = new DomRepresentation(MediaType.APPLICATION_XML, XmlFormatter.jobLogToXml(job, serverAddress));
		return dom;
	}
}
