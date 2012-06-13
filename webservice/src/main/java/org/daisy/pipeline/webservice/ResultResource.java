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

/**
 * The Class ResultResource.
 */
public class ResultResource extends AuthenticatedResource {
	/** The job. */
	private Job job;

	/* (non-Javadoc)
	 * @see org.restlet.resource.Resource#doInit()
	 */
	@Override
    public void doInit() {
		super.doInit();
		if (!isAuthenticated()) {
			return;
		}
		JobManager jobMan = webservice().getJobManager();
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
		// although, does the presence of errors indicate that the result is not available?
		if (zip == null) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
    		return null;
		}

		File zipFile = new File(zip);
		Representation rep = new FileRepresentation(zipFile,MediaType.APPLICATION_ZIP);
		Disposition disposition = new Disposition();
		disposition.setFilename(job.getId().toString() + ".zip");
		disposition.setType(Disposition.TYPE_ATTACHMENT);
		rep.setDisposition(disposition);
		return rep;
    }
}
