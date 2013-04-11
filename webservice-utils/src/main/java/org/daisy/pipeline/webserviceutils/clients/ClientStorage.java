package org.daisy.pipeline.webserviceutils.clients;

import java.util.List;

public interface  ClientStorage {
	List<Client> getAll();
	Client get(String id);
	boolean delete(Client client);
	void update(Client client);
	boolean add(Client client);
}
