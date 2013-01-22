package org.daisy.pipeline.job;

import java.io.IOException;

import org.daisy.common.properties.PropertyPublisher;
import org.daisy.common.properties.PropertyPublisherFactory;

import org.daisy.common.xproc.XProcInput;

import org.daisy.pipeline.event.EventBusProvider;
import org.daisy.pipeline.script.XProcScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobFactory {
	//singleton
	private static JobFactory instance=null;
	private JobMonitorFactory monitorFactory;
	private EventBusProvider eventbusProvider;

	private static final Logger logger = LoggerFactory.getLogger(JobFactory.class);
	public JobFactory(){
		if (instance==null) {
			instance=this;
		}
	}
	public static JobFactory getInstance(){
		new JobFactory();
		return instance;
	}

	public void setJobMonitorFactory(JobMonitorFactory monitorFactory){
		logger.debug("setting monitor factory");
		this.monitorFactory=monitorFactory;
	}
	public void setEventBusProvider(EventBusProvider eventbusProvider){
		logger.debug("setting even bus factory");
		this.eventbusProvider=eventbusProvider;
	}

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
	/**
	 * Creates a new job attached to a context.
	 *
	 * @param script the script
	 * @param input the input
	 * @param context the context
	 * @return the job
	 */
	public Job newJob(XProcScript script, XProcInput input,
			ResourceCollection context) {
		JobId id = JobIdFactory.newId();

		try {

			IOBridge bridge = new IOBridge(id);
			XProcInput resolvedInput = bridge.resolve(script, input, context);
			if(monitorFactory==null) {
				throw new IllegalStateException("No monitor factory");
			}
			JobMonitor monitor=monitorFactory.newJobMonitor(id);
			// TODO validate input
			return new Job(null);//id, script, resolvedInput, bridge,monitor,eventbusProvider.get());
		} catch (IOException e) {
			throw new RuntimeException("Error resolving pipeline info", e);
		}
	}

	public void configure(RuntimeConfigurable runtimeObj){
		if (this.eventbusProvider!=null)
			runtimeObj.setEventBusProvider(this.eventbusProvider);
		
		if(this.monitorFactory!=null)
			runtimeObj.setMonitorFactory(this.monitorFactory);
	}
}
