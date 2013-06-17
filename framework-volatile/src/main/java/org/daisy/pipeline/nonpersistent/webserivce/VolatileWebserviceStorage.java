package org.daisy.pipeline.nonpersistent.webserivce;

import org.daisy.pipeline.webserviceutils.clients.Client;
import org.daisy.pipeline.webserviceutils.clients.ClientStorage;
import org.daisy.pipeline.webserviceutils.requestlog.RequestLog;
import org.daisy.pipeline.webserviceutils.storage.JobConfigurationStorage;
import org.daisy.pipeline.webserviceutils.storage.WebserviceStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VolatileWebserviceStorage   implements WebserviceStorage{
	
	private static final Logger logger = LoggerFactory
			.getLogger(VolatileWebserviceStorage.class);
	private ClientStorage clientStore;
	private RequestLog requestLog;
	private JobConfigurationStorage jobCnfStorage;


	public void activate() {
		logger.debug("Bringing VolatileWebserviceStorage up");
		//this.clientStore = new VolatileClientStorage(this.database);
		//this.requestLog = new VolatileRequestLog(this.database);
		//this.jobCnfStorage=new VolatileJobConfigurationStorage(this.database);
	}

	@Override
	public ClientStorage getClientStorage() {
		return clientStore;
	}

	@Override
	public RequestLog getRequestLog() {
		return requestLog;
	}

	@Override
	public JobConfigurationStorage getJobConfigurationStorage() {
		return this.jobCnfStorage;
	}

}

