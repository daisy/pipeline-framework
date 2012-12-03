package org.daisy.pipeline.persistence.webservice;

import java.util.List;

import org.daisy.pipeline.persistence.Database;
import org.daisy.pipeline.webserviceutils.callback.DefaultCallbackRegistry;
import org.daisy.pipeline.webserviceutils.requestlog.RequestLog;
import org.daisy.pipeline.webserviceutils.requestlog.RequestLogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistentRequestLog implements RequestLog {

	private Database database;
	private static final Logger logger = LoggerFactory.getLogger(PersistentRequestLog.class);
	
	public void activate() {
		logger.debug("activated!");
	}
	
	
	public void setDatabase(Database database) {
		this.database = database;
	}

	@Override
	public boolean contains(RequestLogEntry entry) {
		String queryString = String
				.format(
				"SELECT req FROM PersistentRequestLogEntry AS req WHERE req.clientId='%s' AND req.nonce='%s' AND req.timestamp='%s'",
				entry.getClientId(), entry.getNonce(),
				entry.getTimestamp());

		//TODO check type safety
		List<RequestLogEntry> list = database.runQuery(queryString,
				RequestLogEntry.class);

		return list.size() > 0;
	}

	@Override
	public void add(RequestLogEntry entry) {
		database.addObject(new PersistentRequestLogEntry(entry));
	}

}
