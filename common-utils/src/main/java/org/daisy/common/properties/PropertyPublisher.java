package org.daisy.common.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyPublisher {

	private PropertyTracker tracker;
	private static Logger logger = LoggerFactory.getLogger(PropertyPublisher.class.getName());

	/**
	 * Constructs a new instance.
	 *
	 * @param tracker The tracker for this instance.
	 */
	PropertyPublisher(PropertyTracker tracker) {
		this.tracker = tracker;
	}

	public void publish(Property property){
		if(this.tracker!=null){
			this.tracker.addProperty(property);
		}else{
			logger.warn("Trying to publish a property but the tracker is not set");
		}
	}

	public void unpublish(Property property){
		if(this.tracker!=null){
			this.tracker.deleteProperty(property);
		}else{
			logger.warn("Trying to unpublish a property but the tracker is not set");
		}
	}
	/**
	 * Sets the tracker for this instance.
	 *
	 * @param tracker The tracker.
	 */
	public void setTracker(PropertyTracker tracker) {
		this.tracker = tracker;
	}

}
