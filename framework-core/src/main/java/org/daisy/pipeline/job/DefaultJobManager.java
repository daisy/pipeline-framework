package org.daisy.pipeline.job;

import org.daisy.pipeline.script.BoundXProcScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

/**
 * DefaultJobManager allows to manage the jobs submitted to the daisy pipeline 2
 */
public class DefaultJobManager implements JobManager {

        private static final Logger logger = LoggerFactory
                        .getLogger(DefaultJobManager.class);

        private JobStorage storage;
        //use it to submit and cancel jobs
        private JobExecutionService executionService;
        private JobContextFactory jobContextFactory;

        /**
         * @param storage
         * @param executionService
         */
        public DefaultJobManager(JobStorage storage,
                        JobExecutionService executionService,
                        JobContextFactory jobContextFactory) {
                //check nullities
                this.storage = storage;
                this.executionService = executionService;
                this.jobContextFactory= jobContextFactory;
        }

        protected Optional<Job> newJob(JobContext ctxt) {
                //store it
                Optional<Job> job = this.storage.add(ctxt);
                if(job.isPresent()){
                        //execute it
                        executionService.submit(job.get());
                }
                return job;
        }


        /**
         * This method allows to do some after job creation hook-ups if needed.
         */
        //protected abstract void onNewJob(Job job);

        /* (non-Javadoc)
         * @see org.daisy.pipeline.job.JobManager#getJobIds()
         */
        @Override
        public Iterable<Job> getJobs() {
                return this.storage;
        }

        /**
         * Deletes the job and cleans its context. If you are not using AbstractJobContexts
         * (you have implemented your own JobContexts) you should implement this class and
         * make a custom deletion, otherwise the context won't be cleared out.
         * @see org.daisy.pipeline.job.JobManager#deleteJob(org.daisy.pipeline.job.JobId)
         */
        @Override
        public Optional<Job> deleteJob(JobId id) {
                Optional<Job> job = this.getJob(id);
                if(!job.isPresent()){
                        return Optional.absent();
                }
                this.storage.remove(id);
                if ( job.get().getContext() instanceof AbstractJobContext) {
                        //clean the context
                        ((AbstractJobContext) job.get().getContext()).cleanUp();
                }
                return job;
        }

        /* (non-Javadoc)
         * @see org.daisy.pipeline.job.JobManager#getJob(org.daisy.pipeline.job.JobId)
         */
        @Override
        public Optional<Job> getJob(JobId id) {
                return this.storage.get(id);
        }

        @Override
        public void deleteAll() {
                logger.info("deleting all jobs");
                //iterate over a copy of the jobs, to make sure
                //that we clean the context up
                for (Job job : Iterables.toArray(this.storage, Job.class)) {
                        logger.debug(String.format("Deleting job %s", job));
                        ((AbstractJobContext) job.getContext()).cleanUp();
                        this.storage.remove(job.getId());
                }

        }


        @Override
        public JobManager.JobBuilder newJob(BoundXProcScript boundScript) {
                return new DefaultJobBuilder(boundScript);
        }

        class DefaultJobBuilder implements JobBuilder{
                private BoundXProcScript script;
                private boolean isMapping;
                private ResourceCollection resources;
                private String niceName="";

                /**
                 *
                 */
                public DefaultJobBuilder(BoundXProcScript script) {
                        this.script=script;
                }

                /**
                 * @param isMapping the isMapping to set
                 */
                public JobBuilder isMapping(boolean isMapping) {
                        this.isMapping = isMapping;
                        return this;
                }

                /**
                 * @param resources the resources to set
                 * @return
                 */
                public JobBuilder withResources(ResourceCollection resources) {
                        this.resources = resources;
                        return this;
                }

                /**
                 * @param niceName the niceName to set
                 * @return
                 */
                public JobBuilder withNiceName(String niceName) {
                        this.niceName = niceName;
                        return this;

                }

                public Optional<Job> build(){
                        //use the context factory
                        JobContext ctxt=DefaultJobManager.this.jobContextFactory.
                                newJobContext(this.isMapping,this.niceName,this.script,this.resources);
                        //send to the JobManager
                        return DefaultJobManager.this.newJob(ctxt);
                }


        }

}
