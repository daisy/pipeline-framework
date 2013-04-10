package org.daisy.pipeline.persistence.webservice;

import org.daisy.pipeline.persistence.Database;
import org.daisy.pipeline.webserviceutils.clients.ClientStore;
import org.daisy.pipeline.webserviceutils.requestlog.RequestLog;
import org.daisy.pipeline.webserviceutils.storage.WebserviceStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistentWebserviceStorage implements WebserviceStorage{
	private static final Logger logger = LoggerFactory.getLogger(PersistentWebserviceStorage.class);
	private ClientStore<PersistentClient> clientStore;
	private RequestLog requestLog;
	private Database database;

	public void setDatabase(Database database){
		this.database=database;
	}

	public void activate(){
		logger.debug("Bringing WebserviceStorage up");	
		this.clientStore= new PersistentClientStore(this.database);
		this.requestLog= new PersistentRequestLog(this.database);
	}

	/**
	 * @return the clientStore
	 */
	@Override
	public ClientStore<?> getClientStore() {
		return clientStore;
	}


	/**
	 * @return the requestLog
	 */
	@Override
	public RequestLog getRequestLog() {
		return requestLog;
	}
}
