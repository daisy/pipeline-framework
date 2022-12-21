package org.daisy.pipeline.job;

import java.net.URI;
import java.util.function.Consumer;

import org.daisy.common.messaging.Message.Level;
import org.daisy.common.messaging.MessageBuilder;
import org.daisy.common.priority.Priority;
import org.daisy.common.xproc.XProcEngine;
import org.daisy.common.xproc.XProcErrorException;
import org.daisy.common.xproc.XProcPipeline;
import org.daisy.common.xproc.XProcResult;
import org.daisy.pipeline.clients.Client;
import org.daisy.pipeline.job.impl.JobResultSetBuilder;
import org.daisy.pipeline.job.impl.JobURIUtils;
import org.daisy.pipeline.job.impl.JobUtils;
import org.daisy.pipeline.script.XProcScript;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.MDC;

/**
 * The Class Job defines the execution unit.
 */
public abstract class AbstractJob implements Job {

        private static final Logger logger = LoggerFactory.getLogger(Job.class);

        protected volatile Status status = Status.IDLE;
        protected Priority priority;
        protected AbstractJobContext ctxt;
        public final XProcEngine xprocEngine;
        private final boolean managed = true;

        protected AbstractJob(AbstractJobContext ctxt, Priority priority, XProcEngine xprocEngine) {
                this.ctxt = ctxt;
                this.priority = priority != null ? priority : Priority.MEDIUM;
                this.xprocEngine = xprocEngine;
        }

        @Override
        public JobId getId() {
                return ctxt.getId();
        }

        @Override
        public String getNiceName() {
                return ctxt.getName();
        }

        @Override
        public XProcScript getScript() {
                return ctxt.getScript();
        }

        @Override
        public Status getStatus() {
                return status;
        }

        // for subclasses
        protected synchronized void setStatus(Status status) {
                this.status = status;
        }

        @Override
        public JobMonitor getMonitor() {
                return ctxt.getMonitor();
        }

        @Override
        public URI getLogFile() {
                return ctxt.getLogFile();
        }

        @Override
        public JobResultSet getResults() {
                return ctxt.getResults();
        }

        @Override
        public JobBatchId getBatchId() {
                return ctxt.getBatchId();
        }

        @Override
        public Client getClient() {
                return ctxt.getClient();
        }

        public Priority getPriority() {
                return priority;
        }

        public AbstractJobContext getContext() {
                return ctxt;
        }

        public synchronized final void changeStatus(Status to) {
                logger.info(String.format("Changing job status to: %s", to));
                status = to;
                onStatusChanged();
                if (ctxt.statusListeners != null) {
                        synchronized (ctxt.statusListeners) {
                                for (Consumer<Job.Status> listener : ctxt.statusListeners)
                                        listener.accept(status);
                        }
                }
        }

        // see  ch.qos.logback.classic.ClassicConstants
        private static final Marker FINALIZE_SESSION_MARKER = MarkerFactory.getMarker("FINALIZE_SESSION");

        private boolean run = false;

        @Override
        public final void run() {
                if (managed)
                        throw new UnsupportedOperationException("Managed job can only be run by the JobManager");
                managedRun();
        }

        public synchronized void managedRun() {
                if (run)
                        throw new UnsupportedOperationException("Can not run a job more than once");
                else
                        run = true;

                // used in JobLogFileAppender
                MDC.put("jobid", getId().toString());
                logger.info("Starting to log to job's log file too:" + getId().toString());

                changeStatus(Status.RUNNING);
                XProcPipeline pipeline = null;
                if (ctxt.messageBus == null || xprocEngine == null || ctxt.output == null)
                        // This means we've tried to execute a PersistentJob that was read from the
                        // database. This should not happen because upon creation jobs are
                        // immediately submitted to DefaultJobExecutionService, which keeps them in
                        // memory, and old idle jobs (created but not executed before a shutdown)
                        // are not added to the execution queue upon launching Pipeline.
                        throw new IllegalStateException();
                try {
                        pipeline = xprocEngine.load(this.ctxt.getScript().getXProcPipelineInfo().getURI());
                        XProcResult result = pipeline.run(ctxt.input, () -> ctxt.messageBus, null);
                        result.writeTo(ctxt.output);
                        ctxt.results = JobResultSetBuilder.newResultSet(ctxt.script, ctxt.input, ctxt.output, ctxt.resultMapper);
                        onResultsChanged();
                        if (JobUtils.checkStatusPort(ctxt.script, ctxt.output))
                                changeStatus(Status.SUCCESS);
                        else
                                changeStatus(Status.FAIL);
                } catch (OutOfMemoryError e) {
                        changeStatus( Status.ERROR);
                        ctxt.messageBus.append(new MessageBuilder()
                                               .withLevel(Level.ERROR)
                                               .withText(e.getMessage()))
                                       .close();
                        logger.error("job consumed all heap space", e);
                } catch (Throwable e) {
                        changeStatus( Status.ERROR);
                        ctxt.messageBus.append(new MessageBuilder()
                                               .withLevel(Level.ERROR)
                                               .withText(e.getMessage() + " (Please see detailed log for more info.)"))
                                  .close();
                        if (e instanceof XProcErrorException) {
                                logger.error("job finished with error state\n" + e.toString());
                                logger.debug("job finished with error state", e);
                        } else
                                logger.error("job finished with error state", e);
                }

                logger.info(FINALIZE_SESSION_MARKER,"Stopping logging to job's log file");
                MDC.remove("jobid");
        }

        public void cleanUp() {
                logger.info(String.format("Deleting context for job %s", getId()));
                JobURIUtils.cleanJobBase(getId().toString());
        }

        // for subclasses
        protected void onStatusChanged() {}

        // for subclasses
        protected void onResultsChanged() {}

        @Override
        public boolean equals(Object object) {
                return (object instanceof Job)   && 
                        this.getId().equals(((Job) object).getId());
        }
}
