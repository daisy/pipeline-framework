package org.daisy.pipeline.persistence.webservice;

import java.util.List;

import javax.persistence.NoResultException;

import org.daisy.pipeline.persistence.Database;
import org.daisy.pipeline.webservice.clients.Client;
import org.daisy.pipeline.webservice.clients.ClientStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistentClientStore implements ClientStore {
	
	private static Logger logger = LoggerFactory.getLogger(PersistentClientStore.class);
	
	private Database database;
	
	public PersistentClientStore(){
		logger.debug("created");
	}
	
	public void setDatabase(Database database) {
		this.database = database;
	}

	public List<Client> getAll() {
		String q = String.format("select c from Client as c");
		//TODO check if no DB is present
		//FIXME check type safety
		return database.runQuery(q, Client.class);
	}

	public PersistentClient getClient(String id) {
		String q = String.format("select c from Client as c where c.id='%s'",
				id);
		try {
			//TODO check if no DB is present
			return database.getFirst(q, PersistentClient.class);
		} catch (NoResultException e) { 
			return null;
		}
	}

	@Override
	public Client get(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean delete(Client client) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void update(Client client) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean add(Client client) {
		Client clientExists = get(client.getId());
		if (clientExists != null) {
			logger.warn(String.format("ID %s is already in use.",
					client.getId()));
			return false;
		}

		database.addObject(client);
		return true;
	}

}
