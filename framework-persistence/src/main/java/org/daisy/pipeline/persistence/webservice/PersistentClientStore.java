package org.daisy.pipeline.persistence.webservice;

import java.util.List;

import javax.persistence.NoResultException;

import org.daisy.pipeline.persistence.Database;
import org.daisy.pipeline.webservice.clients.Client;
import org.daisy.pipeline.webservice.clients.ClientStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//public class PersistentClientStore implements ClientStore {
	public class PersistentClientStore implements ClientStore<PersistentClient> {
	
	private static Logger logger = LoggerFactory.getLogger(PersistentClientStore.class);
	
	private Database database;
	
	public PersistentClientStore(){
		logger.debug("created");
	}
	
	public void setDatabase(Database database) {
		this.database = database;
	}

	@Override
//	public Iterable<Client> getAll() {
		public List<PersistentClient> getAll() {
//		public <T extends Client> List<T> getAll() {
//		List<Client> clients = database.runQuery("select c from Client as c", PersistentClient.class);
		return database.runQuery("select c from Client as c", PersistentClient.class);
	}


	@Override
	public PersistentClient get(String id) {
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
