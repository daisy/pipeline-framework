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
import org.daisy.common.xproc.XProcResult;
import org.daisy.pipeline.job.impl.JobUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

/**
 * The Class Job defines the execution unit.
 */
public abstract class AbstractJob implements Job {

        private static final Logger logger = LoggerFactory.getLogger(Job.class);

        private volatile Status status = Status.IDLE;
        private Priority priority;
        protected JobContext ctxt;
        private EventBus eventBus;

        protected AbstractJob(JobContext ctxt, Priority priority) {
                this.ctxt = ctxt;
                this.priority = priority != null ? priority : Priority.MEDIUM;
        }

        @Override
        public Status getStatus() {
                synchronized(this.status){
                        return status;
                }
        }

        protected void setStatus(Status status){
                synchronized(this.status){
                        this.status=status;
                }
        }

        @Override
        public Priority getPriority() {
                return priority;
        }

        /**
         * @return the priority
         */
        protected void setPriority(Priority priority) {
                this.priority=priority;
        }

        public void setEventBus(EventBus eventBus) {
                this.eventBus = eventBus;
        }

        public void setJobMonitor(JobMonitorFactory factory) {
                this.ctxt.setMonitor(factory.newJobMonitor(this.getId(), getStatus() == Status.IDLE));
        }

        @Override
        public JobContext getContext() {
                return this.ctxt;
        }

        /**
         * Sets the ctxt for this instance.
         *
         * @return The ctxt.
         */
        protected void setContext(JobContext ctxt) {
                this.ctxt=ctxt;
        }

        final XProcResult getXProcOutput() {
                return null;
        }

        protected synchronized final void changeStatus(Status to){
                logger.info(String.format("Changing job status to: %s",to));
                this.status=to;
                this.onStatusChanged(to);
                //System.out.println("CHANGING STATUS IN THE DB BEFORE POSTING IT!");
                if (this.eventBus!=null)
                        this.eventBus.post(new StatusMessage.Builder().withJobId(this.getId()).withStatus(this.status).build());
                else
                        logger.warn("I couldnt broadcast my change of status because"+((this.ctxt==null)? " the context ": " event bus ") + "is null");
        }

        private final void broadcastError(String text){
                
                // first close any open message blocks (in this thread)
                MessageAppender m;
                while ((m = MessageAppender.getActiveBlock()) != null)
                        m.close();
                if (this.eventBus != null) {
                        m = new MessageBuilder()
                                .withOwnerId(this.getId().toString())
                                .withLevel(Level.ERROR)
                                .withText(text)
                                .build();
                        m.close();
                        this.eventBus.post((ProgressMessage)m);
                } else
                        logger.warn("I couldnt broadcast an error "+((this.ctxt==null)? " the context ": " event bus ") + "is null");
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
                        XProcResult results = pipeline.run(this.ctxt.getInputs(),this.ctxt.getMonitor(),props);
                        this.ctxt.writeResult(results);
                        //if the validation fails set the job status
                        if (!this.checkStatus()){
                                changeStatus(Status.FAIL);
                        }else{
                                changeStatus(Status.SUCCESS);
                        }
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
        //checks the status returned by the script
        private boolean checkStatus(){
                return JobUtils.checkStatusPort(this.getContext().getScript(), this.getContext().getOutputs());
        }

        @Override
        public boolean equals(Object object) {
                return (object instanceof Job)   && 
                        this.getId().equals(((Job) object).getId());
        }
        
}
