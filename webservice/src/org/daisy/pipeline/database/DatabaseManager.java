package org.daisy.pipeline.database;

import java.net.URI;

import org.daisy.pipeline.persistence.BasicDatabaseManager;
import org.daisy.pipeline.persistence.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseManager extends BasicDatabaseManager {

	private static final String persistenceUnit = "org.daisy.pipeline.database.persistenceUnit";
	private static final String defaultDBName = "PipelineDB";
	private static Logger logger = LoggerFactory
			.getLogger(DatabaseManager.class.getName());

	private static DatabaseManager instance;

	// singleton
	public static DatabaseManager getInstance() {
		if (instance == null) {
			instance = new DatabaseManager();
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

		addObject(newClient);
		return true;
	}

	/*
	 * public boolean isDuplicate(RequestLogEntry entry) { String queryString =
	 * String.format(
	 * "SELECT requestentry FROM RequestLogEntry AS requestentry WHERE requestentry.clientId='%s' AND requestentry.nonce='%s' AND requestentry.timestamp='%s'"
	 * , entry.getClientId(), entry.getNonce(), entry.getTimestamp());
	 * //List<BasicDatabaseObject> list = runQuery(queryString); //if
	 * (list.size() > 0) { // return true; //} return false; }
	 */
	private static String getDefaultDBPath() {
		// TODO is there a better way to find the Pipeline's homedir?
		// java.class.path gives me something like
		// "/Users/marisa/Projects/pipeline2/daisy-pipeline/test/plugins/org.eclipse.equinox.launcher_1.1.0.v20100507.jar"
		String classpath = System.getProperty("java.class.path");
		URI cpuri = URI.create(classpath);
		String relativeDBPath = "../../" + defaultDBName;
		URI dburi = URI.create(relativeDBPath);

		String retval = cpuri.resolve(dburi).toString();

		return retval;
	}

	// TESTING ONLY
	public void addTestData() {
		Client client = new Client();
		client.setId("clientid");
		client.setSecret("supersecret");
		client.setContactInfo("me@example.org");
		client.setRole(Client.Role.ADMIN);

		DatabaseManager.getInstance().addClient(client);
	}
}
