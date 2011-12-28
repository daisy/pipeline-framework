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


/**
 * The Class JobResource.
 */
public class JobResource extends AuthenticatedResource {
	/** The job. */
	private Job job;
	private int msgSeq=0;
	/* (non-Javadoc)
	 * @see org.restlet.resource.Resource#doInit()
	 */
	@Override  
    public void doInit() {  
		super.doInit();
		if (!isAuthenticated()) return;
		JobManager jobMan = ((PipelineWebService)this.getApplication()).getJobManager();
        String idParam = (String) getRequestAttributes().get("id");
        String msgSeqParam = (String) getRequestAttributes().get("msgSeq");
        if (msgSeqParam!=null){
        	msgSeq=Integer.parseInt(msgSeqParam);
        }
        JobId id = JobIdFactory.newIdFromString(idParam);
        job = jobMan.getJob(id);
    }  
  
    /**
     * Gets the resource.
     *
     * @return the resource
     */
    @Get("xml")
    public Representation getResource() {  
    	if (!isAuthenticated()) {
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return null;
    	}
    	if (job == null) {
    		setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			return null;
    	}
	
    	String serverAddress = ((PipelineWebService)this.getApplication()).getServerAddress();
		setStatus(Status.SUCCESS_OK);
		DomRepresentation dom = new DomRepresentation(MediaType.APPLICATION_XML, XmlFormatter.jobToXml(job, serverAddress,msgSeq));
		return dom;
    }  
    
	/**
	 * Delete resource.
	 */
	@Delete
	public void deleteResource() {
		if (!isAuthenticated()) {
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return;
    	}
    	
		if (job == null) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
		}
		
		JobManager jobMan = ((PipelineWebService)this.getApplication()).getJobManager();
		if (jobMan.deleteJob(job.getId())!=null) {
			setStatus(Status.SUCCESS_NO_CONTENT);
		}
		else {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
		}
	}   
    
}
