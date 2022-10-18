package org.daisy.pipeline.job;

import org.daisy.common.xproc.XProcEngine;
import org.daisy.pipeline.clients.Client;
import org.daisy.pipeline.job.impl.DefaultJobExecutionService;
import org.daisy.pipeline.job.impl.DefaultJobManager;
import org.daisy.pipeline.job.impl.JobExecutionService;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(
    name = "job-manager-factory",
    service = { JobManagerFactory.class }
)
public class JobManagerFactory {

        /**
         * Create a job manager for all jobs.
         */
        public JobManager create() {
                return createFor(Client.DEFAULT_ADMIN);
        }

        /**
         * Create a job manager for only the jobs belonging to a certain batch.
         */
        public JobManager createFor(JobBatchId batchId) {
                return createFor(Client.DEFAULT_ADMIN, batchId);
        }

        /**
         * Create a job manager for only the jobs visible for a certain client. An admin client can
         * see all jobs, other clients can only see the jobs that they created.
         *
         * This method is primarily intended to be used by the web service. In other contexts
         * clients make less sence.
         */
        public JobManager createFor(Client client) {
                return new DefaultJobManager(storage.filterBy(client),
                                             executionService.filterBy(client),
                                             new JobContextFactory(client, monitorFactory));
        }

        /**
         * Create a job manager for only the jobs visible for a certain client and belonging to a
         * certain batch. An admin client can see all jobs, other clients can only see the jobs that
         * they created.
         *
         * This method is primarily intended to be used by the web service. In other contexts
         * clients make less sence.
         */
        public JobManager createFor(Client client, JobBatchId batchId) {
                return new DefaultJobManager(storage.filterBy(client).filterBy(batchId),
                                             executionService.filterBy(client),
                                             new JobContextFactory(client, monitorFactory));
        }

        private JobStorage storage;
        private JobMonitorFactory monitorFactory;
        private JobExecutionService executionService;

        @Activate
        protected void init() {
                monitorFactory = new JobMonitorFactory(storage);
        }

        @Reference(
            name = "job-storage",
            unbind = "-",
            service = JobStorage.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.STATIC
        )
        protected void setJobStorage(JobStorage storage) {
                this.storage = storage;
        }

        @Reference(
           name = "xproc-engine",
           unbind = "-",
           service = XProcEngine.class,
           cardinality = ReferenceCardinality.MANDATORY,
           policy = ReferencePolicy.STATIC
        )
        protected void setXProcEngine(XProcEngine xprocEngine) {
                this.executionService = new DefaultJobExecutionService(xprocEngine);
        }
}
