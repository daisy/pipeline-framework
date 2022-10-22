package org.daisy.pipeline.job;

import org.daisy.common.messaging.Message.Level;
import org.daisy.common.messaging.MessageBuilder;
import org.daisy.common.priority.Priority;
import org.daisy.common.xproc.XProcErrorException;
import org.daisy.common.xproc.XProcPipeline;

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

        private volatile Status status = Status.IDLE;
        protected Priority priority;
        protected AbstractJobContext ctxt;
        private final boolean managed = true;

        protected AbstractJob(AbstractJobContext ctxt, Priority priority) {
                this.ctxt = ctxt;
                this.priority = priority != null ? priority : Priority.MEDIUM;
        }

        @Override
        public Status getStatus() {
                return status;
        }

        protected synchronized void setStatus(Status status) {
                this.status = status;
        }

        @Override
        public Priority getPriority() {
                return priority;
        }

        @Override
        public AbstractJobContext getContext() {
                return this.ctxt;
        }

        protected synchronized final void changeStatus(Status to){
                logger.info(String.format("Changing job status to: %s",to));
                this.status=to;
                this.onStatusChanged(to);
                ctxt.changeStatus(this.status);
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
                if (ctxt.messageBus == null || ctxt.xprocEngine == null)
                        // This means we've tried to execute a PersistentJob that was read from the
                        // database. Should not happen.
                        throw new RuntimeException();
                try{
                        pipeline = ctxt.xprocEngine.load(this.ctxt.getScript().getXProcPipelineInfo().getURI());
                        if (ctxt.collectResults(pipeline.run(ctxt.input, () -> ctxt.messageBus, null)))
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

        protected void onStatusChanged(Status newStatus){
                //for subclasses
        }

        @Override
        public boolean equals(Object object) {
                return (object instanceof Job)   && 
                        this.getId().equals(((Job) object).getId());
        }
        
}
