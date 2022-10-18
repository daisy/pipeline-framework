package org.daisy.pipeline.job;

import org.daisy.pipeline.clients.Client;
import org.daisy.pipeline.event.MessageStorage;
import org.daisy.pipeline.job.impl.DefaultJobManager;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(
    name = "job-manager-factory",
    service = { JobManagerFactory.class }
)
public class JobManagerFactory {

        private JobStorage storage;
        private MessageStorage messageStorage;
        private JobExecutionService executionService;
        private JobMonitorFactory monitorFactory;

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
                                             messageStorage,
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
                                             messageStorage,
                                             executionService.filterBy(client),
                                             new JobContextFactory(client, monitorFactory));
        }

        @Reference(
            name = "job-storage",
            unbind = "-",
            service = JobStorage.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.STATIC
        )
        public void setJobStorage(JobStorage storage) {
                //TODO: check null
                this.storage = storage;
        }

        @Reference(
            name = "message-storage",
            unbind = "-",
            service = MessageStorage.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.STATIC
        )
        public void setMessageStorage(MessageStorage storage) {
                this.messageStorage = storage;
        }

        /**
         * @param executionService the executionService to set
         */
        @Reference(
            name = "execution-service",
            unbind = "-",
            service = JobExecutionService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.STATIC
        )
        public void setExecutionService(JobExecutionService executionService) {
                //TODO:check null
                this.executionService = executionService;
        }

        @Reference(
                name = "monitor",
                unbind = "-",
                service = JobMonitorFactory.class,
                cardinality = ReferenceCardinality.MANDATORY,
                policy = ReferencePolicy.STATIC
        )
        public void setJobMonitorFactory(JobMonitorFactory factory){
                this.monitorFactory = factory;
        }
}
