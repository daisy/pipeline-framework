package org.daisy.pipeline.webservice.clients;

import java.util.List;

public interface ClientStore {
	List<Client> getAll();
	Client get(String id);
	boolean delete(Client client);
	void update(Client client);
	boolean add(Client client);

}
