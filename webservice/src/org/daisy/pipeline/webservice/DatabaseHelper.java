package org.daisy.pipeline.webservice;

import java.util.List;

import org.daisy.pipeline.persistence.BasicDatabaseManager;
import org.daisy.pipeline.persistence.Client;
import org.daisy.pipeline.persistence.WSRequestLogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseHelper {

	private static Logger logger = LoggerFactory
			.getLogger(DatabaseHelper.class.getName());

	private static DatabaseHelper instance;

	// singleton
	public static DatabaseHelper getInstance() {
		if (instance == null) {
			instance = new DatabaseHelper();
		}
		return instance;
	}

	public boolean addClient(Client newClient) {

		Client clientExists = Client.getClient(newClient.getId());
		if (clientExists != null) {
			logger.warn(String.format("ID %s is already in use.",
					newClient.getId()));
			return false;
		}

		new BasicDatabaseManager().addObject(newClient);
		return true;
	}


	 public boolean isDuplicate(WSRequestLogEntry entry) {
		 String queryString = String.format(
				 "SELECT requestentry FROM WSRequestLogEntry AS requestentry WHERE requestentry.clientId='%s' AND requestentry.nonce='%s' AND requestentry.timestamp='%s'",
				 entry.getClientId(), entry.getNonce(), entry.getTimestamp());

		 List<WSRequestLogEntry> list = new BasicDatabaseManager().runQuery(queryString, WSRequestLogEntry.class);

		 return list.size() > 0;
	 }


	// TESTING ONLY
	public void addTestData() {

		List<WSRequestLogEntry> l = new BasicDatabaseManager().runQuery("SELECT r from WSRequestLogEntry as r", WSRequestLogEntry.class);

		Client client = new Client();
		client.setId("clientid");
		client.setSecret("supersecret");
		client.setContactInfo("me@example.org");
		client.setRole(Client.Role.ADMIN);

		DatabaseHelper.getInstance().addClient(client);
	}
}
