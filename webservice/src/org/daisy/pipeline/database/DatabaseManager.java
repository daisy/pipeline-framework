package org.daisy.pipeline.database;

import java.util.ArrayList;
import java.util.List;

// dummy class to mimic the eventual "real" persistence db version
public class DatabaseManager {

private static DatabaseManager instance;

	// singleton
	public static DatabaseManager getInstance() {
		if (instance == null) {
			instance = new DatabaseManager();
		}
		return instance;
	}

	private final Client theOnlyClient;

	private DatabaseManager() {
		theOnlyClient = new Client();
		theOnlyClient.setId("clientid");
		theOnlyClient.setSecret("supersecret");
		theOnlyClient.setRole(Client.Role.ADMIN);
	}
	public Client getClientById(String id) {
		return theOnlyClient;
	}
	public boolean addClient(Client client) {
		return true;
	}
	public boolean isDuplicate(RequestLogEntry entry) {
		return false;
	}

	public void addObject(Object obj) {
		return;
	}

	public boolean deleteObject(Object obj) {
		return true;
	}

	public void updateObject(String str, Object obj) {
		return;
	}

	public List<BasicDatabaseObject> runQuery(String str) {
		return new ArrayList<BasicDatabaseObject>();
	}
}
