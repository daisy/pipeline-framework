package org.daisy.pipeline.job;

import java.io.IOException;

import org.daisy.common.properties.PropertyPublisher;
import org.daisy.common.properties.PropertyPublisherFactory;
import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcOutput;
import org.daisy.pipeline.event.EventBusProvider;
import org.daisy.pipeline.script.BoundXProcScript;
import org.daisy.pipeline.script.XProcScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a proxified singleton so we can cope with 
 * osgi easily. 
 *
 * This means that all the 'this' calls have been
 * replaced by INSTANCE.
 * This allows osgi create an instance of the object (or any other class)
 * using the public constructor but all the changes will be performed over the 
 * static instance
 *
 * 
 *
 */
public final class JobContextFactory {
	private static final Logger logger = LoggerFactory.getLogger(JobContextFactory.class);

	private JobMonitorFactory monitorFactory;
	private EventBusProvider eventbusProvider;


	public JobContextFactory() {
		//nothing
	}

	public JobContext newMappingJobContext(String niceName,BoundXProcScript boundedScript,ResourceCollection collection){
		JobId id = JobIdFactory.newId();
		AbstractJobContext ctxt=null;
		try{
			 ctxt=new MappingJobContext(id,niceName,boundedScript,collection);
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
		AbstractJobContext ctxt=new SimpleJobContext(id,niceName,boundScript);
		this.configure(ctxt);
		return ctxt;

	}
	public void setJobMonitorFactory(JobMonitorFactory monitorFactory){
			logger.debug("setting monitor factory");
			this.monitorFactory=monitorFactory;
		
	}
	public void setEventBusProvider(EventBusProvider eventbusProvider){
			logger.debug("setting even bus factory");
			this.eventbusProvider=eventbusProvider;
	}

	//FIXME: probably move these two methods somewhere else, maybe a dummy class for the framework just tu publish this.
	public void setPropertyPublisherFactory(PropertyPublisherFactory propertyPublisherFactory){
		PropertyPublisher propertyPublisher=propertyPublisherFactory.newPropertyPublisher();	
		//the property publishing step goes here
		propertyPublisher.publish("org.daisy.pipeline.iobase" ,System.getProperty("org.daisy.pipeline.iobase","" ),this.getClass());
		propertyPublisher.publish("org.daisy.pipeline.home" ,System.getProperty("org.daisy.pipeline.home","" ),this.getClass());
		propertyPublisher.publish("org.daisy.pipeline.logdir",System.getProperty("org.daisy.pipeline.logdir","" ),this.getClass());
	}

	public void unsetPropertyPublisherFactory(PropertyPublisherFactory propertyPublisherFactory){
		PropertyPublisher propertyPublisher=propertyPublisherFactory.newPropertyPublisher();	
		//the property unpublishing step goes here
		propertyPublisher.unpublish("org.daisy.pipeline.iobase" ,  this.getClass());
		propertyPublisher.unpublish("org.daisy.pipeline.home"   ,  this.getClass());
		propertyPublisher.unpublish("org.daisy.pipeline.logdir" ,  this.getClass());

	}

	public void configure(RuntimeConfigurable runtimeObj){

		logger.debug(String.format("configuring object %s",runtimeObj));
		runtimeObj.setEventBusProvider(this.eventbusProvider);
		runtimeObj.setMonitorFactory(this.monitorFactory);
		

	}

}
