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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class LogResource.
 */

public class LogResource extends AuthenticatedResource {
	/** The job. */
	private Job job;
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(XmlFormatter.class.getName());

	/* (non-Javadoc)
	 * @see org.restlet.resource.Resource#doInit()
	 */
	@Override
    public void doInit() {
		super.doInit();
		if (!isAuthenticated()) {
			return;
		}

		JobManager jobMan = ((PipelineWebService)getApplication()).getJobManager();
        String idParam = (String) getRequestAttributes().get("id");

        try {
        	JobId id = JobIdFactory.newIdFromString(idParam);
        	job = jobMan.getJob(id);
        }
        catch(Exception e) {
        	logger.error(e.getMessage());
        	job = null;
        }

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

    	setStatus(Status.SUCCESS_OK);

    	FileRepresentation logfile;
    	JobResult result = job.getResult();
    	if (result == null){
    		return null;
    	}
    	URI logfileUri = result.getLogFile();
    	if (logfileUri != null) {
    		logfile = new FileRepresentation(new File(logfileUri), MediaType.TEXT_PLAIN);
    		return logfile;
    	} else {
			return null;
        }
	}
}
