package org.daisy.pipeline.webservice;

import java.io.File;
import java.net.URI;

import java.util.Collection;

import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.job.JobIdFactory;
import org.daisy.pipeline.job.JobManager;
import org.daisy.pipeline.job.JobResult;
import org.daisy.pipeline.job.ResultSet;

import org.restlet.data.Disposition;
import org.restlet.data.MediaType;
import org.restlet.data.Status;

import org.restlet.representation.FileRepresentation;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ResultResource.
 */
public class ResultResource extends AuthenticatedResource {
	/** The job. */
	private Job job;
	private static Logger logger = LoggerFactory.getLogger(ResultResource.class
			.getName());

	/*
	 * (non-Javadoc)
	 *
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
		try {
			JobId id = JobIdFactory.newIdFromString(idParam);
			job = jobMan.getJob(id);
		} catch (Exception e) {
			logger.debug("Job Id malformed - Job not found: " + idParam);
			job = null;
		}
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
			return this.getErrorRepresentation("Job not found");
		}

		if (!job.getStatus().equals(Job.Status.DONE)) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			return this.getErrorRepresentation("Job status differnt to DONE");
		}

		Collection<JobResult> results = job.getContext().getResults().getResults();
		if (results.size() == 0) {
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return this.getErrorRepresentation("No results available");
		}
		try{
			Representation rep = new InputRepresentation(ResultSet.asZip(results),
					MediaType.APPLICATION_ZIP);
			Disposition disposition = new Disposition();
			disposition.setFilename(job.getId().toString() + ".zip");
			disposition.setType(Disposition.TYPE_ATTACHMENT);
			rep.setDisposition(disposition);
			return rep;
		}catch(Exception e){
				setStatus(Status.SERVER_ERROR_INTERNAL);
				return this.getErrorRepresentation(e);
		}
	}
}
