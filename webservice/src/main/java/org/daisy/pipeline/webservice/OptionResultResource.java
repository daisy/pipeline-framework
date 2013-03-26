package org.daisy.pipeline.webservice;

import java.util.Collection;

import javax.xml.namespace.QName;

import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.result.JobResult;

/**
 * The Class ResultResource.
 */
public class OptionResultResource extends NamedResultResource {

	@Override
	protected Collection<JobResult> gatherResults(Job job, String name) {
		return job.getContext().getResults().getResults(new QName(name));
	}
}
