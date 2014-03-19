
package org.daisy.pipeline.webservice;



import java.util.Collection;

import org.daisy.pipeline.job.PrioritizedJob;
import org.daisy.pipeline.webserviceutils.xml.QueueXmlWriter;
import org.daisy.pipeline.webserviceutils.xml.XmlWriterFactory;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class QueueResource extends AdminResource {

        private static final Logger logger = LoggerFactory.getLogger(QueueResource.class);
	@Override
	public void doInit() {
		super.doInit();
		if (!isAuthorized()) {
			return;
		}
	}

	/**
	 * List the jobs, their final priorities and their times
	 *
	 * @return the resource
	 */
	@Get("xml")
	public Representation getResource() {
		if (!isAuthorized()) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return null;
		}

		setStatus(Status.SUCCESS_OK);
                Collection<PrioritizedJob> jobs=webservice().getJobManager(this.getClient()).getExecutionQueue().asCollection();
                logger.debug("Queue size: "+jobs.size());
		QueueXmlWriter writer = XmlWriterFactory.createXmlWriterForQueue(jobs);
                DomRepresentation dom = new DomRepresentation(MediaType.APPLICATION_XML,
                                writer.getXmlDocument());
		return dom;
	}

}
