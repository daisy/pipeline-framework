package org.daisy.pipeline.job;

import java.util.Properties;

import org.daisy.common.messaging.Message.Level;
import org.daisy.common.messaging.MessageAppender;
import org.daisy.common.messaging.MessageBuilder;
import org.daisy.common.messaging.ProgressMessage;
import org.daisy.common.priority.Priority;
import org.daisy.common.xproc.XProcEngine;
import org.daisy.common.xproc.XProcErrorException;
import org.daisy.common.xproc.XProcPipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class Job defines the execution unit.
 */
public abstract class AbstractJob implements Job {

        private static final Logger logger = LoggerFactory.getLogger(Job.class);

        private volatile Status status = Status.IDLE;
        protected Priority priority;
        protected AbstractJobContext ctxt;

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
                ctxt.getMonitor().getEventBus().post(
                        new StatusMessage.Builder().withJobId(this.getId()).withStatus(this.status).build());
        }

        private final void broadcastError(String text){
                
                // first close any open message blocks (in this thread)
                MessageAppender m;
                while ((m = MessageAppender.getActiveBlock()) != null)
                        m.close();
                m = new MessageBuilder()
                        .withOwnerId(this.getId().toString())
                        .withLevel(Level.ERROR)
                        .withText(text)
                        .build();
                m.close();
                ctxt.getMonitor().getEventBus().post((ProgressMessage)m);
        }

        @Override
        public synchronized final void run(XProcEngine engine) {
                changeStatus(Status.RUNNING);
                XProcPipeline pipeline = null;
                try{
                        pipeline = engine.load(this.ctxt.getScript().getXProcPipelineInfo().getURI());
                        Properties props = new Properties();
                        props.setProperty("JOB_ID", this.ctxt.getId().toString()); // used in calabash-adapter's EventBusMessageListener
                        props.setProperty(
                                "autonamesteps",
                                org.daisy.pipeline.properties.Properties.getProperty(
                                        "org.daisy.pipeline.calabash.autonamesteps", "false"));
                        if (ctxt.collectResults(pipeline.run(ctxt.input, ctxt.getMonitor(), props)))
                                changeStatus(Status.SUCCESS);
                        else
                                changeStatus(Status.FAIL);
                }catch(Exception e){
                        changeStatus( Status.ERROR);
                        broadcastError(e.getMessage() + " (Please see detailed log for more info.)");
                        if (e instanceof XProcErrorException) {
                                logger.error("job finished with error state\n" + e.toString());
                                logger.debug("job finished with error state", e);
                        } else
                                logger.error("job finished with error state", e);
		} catch (OutOfMemoryError e) {//this one needs it's own catch!
                        changeStatus( Status.ERROR);
                        broadcastError(e.getMessage());
                        logger.error("job consumed all heap space",e);
                }

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
