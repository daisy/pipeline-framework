package org.daisy.pipeline.nonpersistent.webserivce;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.daisy.pipeline.webserviceutils.clients.Client;
import org.daisy.pipeline.webserviceutils.clients.ClientStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VolatileClientStorage implements ClientStorage {
	
	private static Logger logger = LoggerFactory.getLogger(VolatileClientStorage.class);
	private Map<String, Client> clients;

	
	public VolatileClientStorage(){
		this.clients=Collections.synchronizedMap(new HashMap<String,Client>());
	}
	

	@Override
	public List<? extends Client> getAll() {
		return Collections.unmodifiableList(new LinkedList<Client>(this.clients.values()));
	}


	@Override
	public Client get(String id) {
		return this.clients.get(id);
	}


	@Override
	public boolean delete(Client client) {
		return this.clients.remove(client.getId())!=null;
	}

	@Override
	public boolean update(Client client) {
		if (this.clients.containsKey(client.getId())){
			this.clients.put(client.getId(),client);
			return true;
		}else{
			return false;
		}
	}

	@Override
	public boolean add(Client client) {
		if (this.clients.containsKey(client.getId())){
			return false;
		}else{
			this.clients.put(client.getId(),client);
			return true;
		}
	}


}
