package org.daisy.pipeline.persistence.impl.job;

import javax.persistence.EntityManagerFactory;

import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.job.JobMonitor;
import org.daisy.pipeline.job.JobMonitorFactory;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(
	name = "persistent-monitor",
	immediate = true,
	service = { JobMonitorFactory.class }
)
public class PersistentJobMonitorFactory implements JobMonitorFactory {

	private EntityManagerFactory emf;

	@Override
	public JobMonitor newJobMonitor(JobId id) {
		return new PersistentJobMonitor(id, emf);
	}

	@Reference(
		name = "entity-manager-factory",
		unbind = "-",
		service = EntityManagerFactory.class,
		target = "(osgi.unit.name=pipeline-pu)",
		cardinality = ReferenceCardinality.MANDATORY,
		policy = ReferencePolicy.STATIC
	)
	public void setEntityManagerFactory(EntityManagerFactory emf) {
		this.emf = emf;
	}

}
