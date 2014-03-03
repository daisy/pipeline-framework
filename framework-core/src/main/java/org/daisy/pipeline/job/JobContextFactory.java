package org.daisy.pipeline.job;

import java.io.IOException;

import org.daisy.common.properties.PropertyPublisher;
import org.daisy.common.properties.PropertyPublisherFactory;
import org.daisy.pipeline.clients.Client;
import org.daisy.pipeline.event.EventBusProvider;
import org.daisy.pipeline.script.BoundXProcScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

/**
 *
 *
 */
public class JobContextFactory {

        private static final Logger logger = LoggerFactory.getLogger(JobContextFactory.class);

        private JobMonitorFactory monitorFactory;

        private Client client;


        public JobContextFactory(JobMonitorFactory monitorFactory,Client client) {
                this.monitorFactory=monitorFactory;
                this.client=client;
        }

         
        public JobContext newJobContext(boolean mapping,String niceName,BoundXProcScript boundScript,ResourceCollection collection){
                //if mapping create a new mapping context
                if(mapping){
                        return this.newMappingJobContext(niceName,boundScript,collection);
                }else{
                //otherwise create a simple one
                        return this.newJobContext(niceName,boundScript);
                }
        }

        public JobContext newMappingJobContext(String niceName,BoundXProcScript boundScript,ResourceCollection collection){
                JobId id = JobIdFactory.newId();
                AbstractJobContext ctxt=null;
                try{
                         ctxt=new MappingJobContext(client,id,niceName,boundScript,collection);
                }catch (IOException ex){
                        throw new RuntimeException("Error while creating MappingJobContext",ex);
                }
                this.configure(ctxt);
                return ctxt;

        }

        public JobContext newMappingJobContext(String niceName,BoundXProcScript boundScript){
                return newMappingJobContext(niceName,boundScript,null);
        }

        public JobContext newJobContext(String niceName,BoundXProcScript boundScript){
                JobId id = JobIdFactory.newId();
                AbstractJobContext ctxt=new SimpleJobContext(this.client,id,niceName,boundScript);
                this.configure(ctxt);
                return ctxt;

        }

        public void configure(RuntimeConfigurable runtimeObj){
                logger.debug(String.format("configuring object %s",runtimeObj));
                runtimeObj.setMonitorFactory(this.monitorFactory);
        }

}
