package org.daisy.pipeline.database;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

// dummy class to mimic the eventual "real" persistence db version
public class DatabaseManager {

private static DatabaseManager instance;

	List<Client> clients;

	// singleton
	public static DatabaseManager getInstance() {
		if (instance == null) {
			instance = new DatabaseManager();
		}
		return instance;
	}

	private DatabaseManager() {
		clients = new ArrayList<Client>();
	}
	public Client getClientById(String id) {
		for (Client client : clients) {
			if (client.getId().equals(id)) {
				return client;
			}
		}
		return null;
	}
	public boolean addClient(Client client) {
		clients.add(client);
		return true;
	}
	// TODO temporary function in place of actual DB
	public List<Client> getClients() {
		return clients;
	}
	public boolean isDuplicate(RequestLogEntry entry) {
		// TODO stub function in place of actual DB
		return false;
	}

	public void addObject(Object obj) {
		// TODO stub function in place of actual DB
	}

	public boolean deleteObject(Object obj) {
		// TODO stub function in place of actual DB
		return true;
	}

	public void updateObject(String str, Object obj) {
		// TODO stub function in place of actual DB
		return;
	}

	public List<BasicDatabaseObject> runQuery(String str) {
		// TODO stub function in place of actual DB
		return null;
	}

	public void loadData(Document doc) {
		Element clientsElm = doc.getDocumentElement();
		NodeList clientElms = clientsElm.getElementsByTagName("client");
		for (int i = 0; i<clientElms.getLength(); i++) {
			Element clientElm = (Element)clientElms.item(i);
			Client client = new Client();
			client.setId(clientElm.getAttribute("id"));
			client.setRole(Client.Role.valueOf(clientElm.getAttribute("role")));
			client.setContactInfo(clientElm.getAttribute("contact"));
			client.setSecret(clientElm.getAttribute("secret"));
			addClient(client);
		}
	}
}
