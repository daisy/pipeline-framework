package org.daisy.pipeline.persistence.impl.job;

import java.util.Iterator;

import javax.persistence.CacheStoreMode;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.daisy.common.priority.Priority;
import org.daisy.pipeline.clients.Client;
import org.daisy.pipeline.clients.Client.Role;
import org.daisy.pipeline.event.EventBusProvider;
import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.JobBatchId;
import org.daisy.pipeline.job.JobContext;
import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.job.JobMonitorFactory;
import org.daisy.pipeline.job.JobStorage;
import org.daisy.pipeline.persistence.impl.Database;
import org.daisy.pipeline.properties.Properties;
import org.daisy.pipeline.script.ScriptRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Collections2;
import com.google.common.eventbus.EventBus;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component(
    name = "persistent-job-storage",
    service = { JobStorage.class }
)
public class PersistentJobStorage implements JobStorage {

        private static final boolean PERSISTENCE_DISABLED = "false".equalsIgnoreCase(
                    Properties.getProperty("org.daisy.pipeline.persistence"));
        private final static String STORE_MODE="javax.persistence.cache.storeMode";
        private static final Logger logger = LoggerFactory
                        .getLogger(PersistentJobStorage.class);

        private Database db;

        private EventBus eventBus;
        private JobMonitorFactory jobMonitorFactory;

        private QueryDecorator<PersistentJob> filter;

        public PersistentJobStorage(){}

        PersistentJobStorage(Database db, QueryDecorator<PersistentJob> filter, EventBus eventBus, JobMonitorFactory jobMonitorFactory) {
                this.db=db;
                this.filter=filter;
                this.eventBus = eventBus;
                this.jobMonitorFactory = jobMonitorFactory;
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
                this.db = new Database(emf);
                this.filter=QueryDecorator.empty(db.getEntityManager());
        }

        @Reference(
           name = "script-registry",
           unbind = "-",
           service = ScriptRegistry.class,
           cardinality = ReferenceCardinality.MANDATORY,
           policy = ReferencePolicy.STATIC
        )
        public void setRegistry(ScriptRegistry scriptRegistry) {
                PersistentJobContext.setScriptRegistry(scriptRegistry);
        }

        @Reference(
            name = "event-bus-provider",
            unbind = "-",
            service = EventBusProvider.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.STATIC
        )
        public void setEventBus(EventBusProvider provider) {
                eventBus = provider.get();
        }

        @Reference(
            name = "monitor",
            unbind = "-",
            service = JobMonitorFactory.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.STATIC
        )
        public void setJobMonitorFactory(JobMonitorFactory factory){
                jobMonitorFactory = factory;
        }

        /**
         * @throws RuntimeException if persistent storage is disabled through the org.daisy.pipeline.persistence system property.
         */
        @Activate
        protected void activate() throws RuntimeException {
                if (PERSISTENCE_DISABLED)
                        throw new RuntimeException("Persistent storage is disabled");
        }

        private void checkDatabase() {
                if (db == null) {
                        logger.warn("Database is null in persistent job storage");
                        throw new IllegalStateException("db is null");
                }
        }

        @Override
        public Iterator<Job> iterator() {
                checkDatabase();
                TypedQuery<PersistentJob> query=this.filter.getQuery(PersistentJob.class);
                //make sure that we have the data from the db, 
                query.setHint(STORE_MODE, CacheStoreMode.REFRESH);

                return Collections2.transform(query.getResultList(),
                                new Function<PersistentJob,Job>() {
                                        @Override
                                        public Job apply(PersistentJob job) {
                                                // set event bus and monitor
                                                if (PersistentJobStorage.this.jobMonitorFactory != null)
                                                        job.setJobMonitor(PersistentJobStorage.this.jobMonitorFactory);
                                                if (PersistentJobStorage.this.eventBus != null)
                                                        job.setEventBus(PersistentJobStorage.this.eventBus);
                                                return job;
                                        }
                                }).iterator();
        }


        @Override
        public Optional<Job> add(Priority priority,JobContext ctxt) {
                checkDatabase();
                logger.debug("Adding job to db:" + ctxt.getId());
                PersistentJob pjob = new PersistentJob(db, ctxt, priority);
                // set event bus and monitor
                if (jobMonitorFactory != null)
                        pjob.setJobMonitor(jobMonitorFactory);
                if (eventBus != null)
                        pjob.setEventBus(eventBus);
                return Optional.of(pjob);
        }

        @Override
        public Optional<Job> remove(JobId jobId) {
                checkDatabase();
                Optional<Job> stored=this.get(jobId);
                if (stored.isPresent()) {
                        db.deleteObject(stored.get());
                        logger.debug(String.format("Job with id %s deleted", jobId));
                }
                return stored;
        }

        @Override
        public Optional<Job> get(JobId id) {
                checkDatabase();
                IdFilter idFilter=new IdFilter(this.db.getEntityManager(),id);
                idFilter.setNext(this.filter);

                PersistentJob job = null;
                try{
                       job= idFilter.getQuery(PersistentJob.class).getSingleResult();
                }catch(NoResultException nre){
                        return Optional.absent();
                }

                if (job != null) {
                        job.setDatabase(db);
                        // set event bus and monitor
                        if (jobMonitorFactory != null)
                                job.setJobMonitor(jobMonitorFactory);
                        if (eventBus != null)
                                job.setEventBus(eventBus);
                }
                return Optional.fromNullable((Job)job);
        }

        @Override
        public JobStorage filterBy(Client client) {
                if (client.getRole()==Role.ADMIN){
                        return this;
                }else{
                        QueryDecorator<PersistentJob> byClient= new ClientFilter(this.db.getEntityManager(),client);
                        byClient.setNext(filter);
                        return new PersistentJobStorage(db, byClient, eventBus, jobMonitorFactory);
                }
        }

        @Override
        public JobStorage filterBy(JobBatchId id) {
                QueryDecorator<PersistentJob> byBatchId= new BatchFilter(this.db.getEntityManager(),id);
                byBatchId.setNext(filter);
                return new PersistentJobStorage(db, byBatchId, eventBus, jobMonitorFactory);
        }
        
}
