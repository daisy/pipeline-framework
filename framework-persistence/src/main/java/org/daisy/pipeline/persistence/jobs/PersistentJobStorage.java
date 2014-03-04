package org.daisy.pipeline.persistence.jobs;

import java.util.Iterator;

import org.daisy.pipeline.clients.Client;
import org.daisy.pipeline.event.EventBusProvider;
import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.Job.JobBuilder;
import org.daisy.pipeline.job.JobContext;
import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.job.JobStorage;
import org.daisy.pipeline.persistence.Database;
import org.daisy.pipeline.script.ScriptRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.eventbus.EventBus;

public class PersistentJobStorage implements JobStorage {

        private static final Logger logger = LoggerFactory
                        .getLogger(PersistentJobStorage.class);

        private Database db;
        private EventBus eventBus;

        public void setDatabase(Database db) {
                this.db = db;
        }

        public void setRegistry(ScriptRegistry scriptRegistry) {
                PersistentJobContext.setScriptRegistry(scriptRegistry);
        }

        public void setEventBus(EventBusProvider provider) {
                this.eventBus= provider.get();
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
                //sets the event bus for all the jobs returned
                return Collections2.transform(PersistentJob.getAllJobs(this.db),
                                new Function<Job, Job>() {
                                        @Override
                                        public Job apply(Job job) {
                                                job.setEventBus(PersistentJobStorage.this.eventBus);
                                                return job;
                                        }
                                }).iterator();
        }

        @Override
        public Job add(JobContext ctxt) {
                checkDatabase();
                logger.debug("Adding job to db:" + ctxt.getId());
                JobBuilder builder = new PersistentJob.PersistentJobBuilder(db)
                                .withContext(ctxt).withEventBus(this.eventBus);
                Job pjob = builder.build();
                this.ctxtFactory.configure((PersistentJobContext) pjob.getContext());
                return pjob;
        }

        @Override
        public Job remove(JobId jobId) {
                checkDatabase();
                Job job = db.getEntityManager().find(PersistentJob.class,
                                jobId.toString());
                if (job != null) {
                        db.deleteObject(job);
                        logger.debug(String.format("Job with id %s deleted", jobId));
                }
                return job;
        }

        @Override
        public Job get(JobId id) {
                checkDatabase();
                PersistentJob job = null;
                job = db.getEntityManager().find(PersistentJob.class, id.toString());
                if (job != null) {
                        job.setDatabase(db);
                        job.setEventBus(this.eventBus);
                        this.ctxtFactory.configure((PersistentJobContext) job.getContext());
                }
                return job;
        }

        @Override
        public JobStorage filterBy(Client client) {
                // TODO Auto-generated method stub
                return null;
        }
        
}
