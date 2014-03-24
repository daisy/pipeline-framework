package org.daisy.pipeline.job;

import org.daisy.common.xproc.XProcEngine;
import org.daisy.pipeline.clients.Client;
import org.daisy.pipeline.clients.Client.Role;
import org.daisy.pipeline.job.fuzzy.FuzzyJobFactory;
import org.daisy.pipeline.job.priority.ForwardingPrioritableRunnable;
import org.daisy.pipeline.job.priority.PrioritizableRunnable;
import org.daisy.pipeline.job.priority.PriorityThreadPoolExecutor;
import org.daisy.pipeline.job.priority.timetracking.TimeFunctions;
import org.daisy.pipeline.job.priority.timetracking.TimeTrackerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.google.common.base.Predicate;

/**
 * DefaultJobExecutionService is the defualt way to execute jobs
 */
public class DefaultJobExecutionService implements JobExecutionService {

        /** The Constant logger. */
        private static final Logger logger = LoggerFactory
                        .getLogger(DefaultJobExecutionService.class);
        /** The xproc engine. */
        private XProcEngine xprocEngine;

        private PriorityThreadPoolExecutor executor = PriorityThreadPoolExecutor
                        .newFixedSizeThreadPoolExecutor(
                                        2,
                                        TimeTrackerFactory.newFactory(3,
                                                        TimeFunctions.newLinearTimeFunctionFactory()));
        private ExecutionQueue executionQueue;

        public DefaultJobExecutionService(){
                this.executionQueue=new DefaultExecutionQueue(executor); 
        }
        /**
         * @param xprocEngine
         * @param executor
         * @param executionQueue
         */
        public DefaultJobExecutionService(XProcEngine xprocEngine,
                        PriorityThreadPoolExecutor executor, ExecutionQueue executionQueue) {
                this.xprocEngine = xprocEngine;
                this.executor = executor;
                this.executionQueue = executionQueue;
        }

        /**
         * Sets the x proc engine.
         *
         * @param xprocEngine
         *            the new x proc engine
         */
        public void setXProcEngine(XProcEngine xprocEngine) {
                this.xprocEngine = xprocEngine;
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * org.daisy.pipeline.job.JobExecutionService#submit(org.daisy.pipeline.
         * job.Job)
         */
        @Override
        public void submit(final Job job) {
                //logger.info("Submitting job");
                //Make the runnable ready to submit to the fuzzy-prioritized thread pool
                PrioritizableRunnable runnable = FuzzyJobFactory.newFuzzyRunnable(job,
                                this.getRunnable(job));
                //Conviniently wrap it in a PrioritizedJob for later access
                this.executor.execute(new RunnablePrioritizedJob(runnable, job));
        }

        Runnable getRunnable(final Job job) {
                return new ThreadWrapper(new Runnable() {

                        @Override
                        public void run() {

                                try {
                                        logger.info("Starting to log to job's log file too:"
                                                        + job.getId().toString());
                                        MDC.put("jobid", job.getId().toString());
                                        job.run(xprocEngine);
                                        MDC.remove("jobid");
                                        logger.info("Stopping logging to job's log file");
                                } catch (Exception e) {
                                        throw new RuntimeException(e.getCause());
                                }

                        }
                });
        }

        /**
         * This class offers a solution to avoid memory leaks due to
         * the missuse of ThreadLocal variables.
         * The actual run implementation may be a little bit naive regarding the interrupt handling
         *
         */
        private static class ThreadWrapper implements Runnable {

                private static final Logger logger = LoggerFactory
                                .getLogger(ThreadWrapper.class);
                private Runnable runnable;

                /**
                 * Constructs a new instance.
                 *
                 * @param runnable The runnable for this instance.
                 */
                public ThreadWrapper(Runnable runnable) {
                        this.runnable = runnable;
                }

                public void run() {
                        logger.info("Starting wrappedThread :"
                                        + Thread.currentThread().getName());
                        Thread t = new Thread(this.runnable);
                        t.start();
                        try {
                                t.join();
                        } catch (InterruptedException e) {
                                logger.warn("ThreadWrapper was interrupted...");
                        }
                }

        }

        protected PriorityThreadPoolExecutor getExecutor() {
                return this.executor;
        }

        /**
         * Wrapps the runnable with the associated job to expose the PrioritizedJob interface
         */
        static class RunnablePrioritizedJob extends ForwardingPrioritableRunnable
                        implements PrioritizedJob {
                private Job job;

                public RunnablePrioritizedJob(PrioritizableRunnable delegate, Job job) {
                        super(delegate);
                        this.job = job;
                }

                /**
                 * @return the job
                 */
                @Override
                public Job getJob() {
                        return job;
                }

                public PrioritizableRunnable asPrioritizableRunnable() {
                        return this;
                }

        }

        @Override
        public ExecutionQueue getExecutionQueue() {
                return new DefaultExecutionQueue(this.executor);

        }

        @Override
        public JobExecutionService filterBy(final Client client) {
                if (client.getRole()==Role.ADMIN){
                        return this;
                }else{
                        return new DefaultJobExecutionService(this.xprocEngine, this.executor, 
                                        new FilteredExecutionQueue(this.executor,
                                                new Predicate<PrioritizedJob>() {
                                                        @Override
                                                        public boolean apply(PrioritizedJob pJob) {
                                                                return pJob.getJob().getContext()
                                                .getClient().getId().equals(client.getId());
                                                        }
                                                }
                        ));
                }
        }
}
