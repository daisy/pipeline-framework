package org.daisy.pipeline.persistence.webservice;

import java.util.List;

import org.daisy.pipeline.persistence.Database;
import org.daisy.pipeline.webservice.requestlog.RequestLog;
import org.daisy.pipeline.webservice.requestlog.RequestLogEntry;

public class PersistentRequestLog implements RequestLog {

	private Database database;

	public void setDatabase(Database database) {
		this.database = database;
	}

	@Override
	public boolean contains(RequestLogEntry entry) {
		String queryString = String
				.format("SELECT requestentry FROM WSRequestLogEntry AS requestentry WHERE requestentry.clientId='%s' AND requestentry.nonce='%s' AND requestentry.timestamp='%s'",
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
