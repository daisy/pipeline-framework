package org.daisy.pipeline.clients;

import java.util.List;

public interface  ClientStorage {
	List<? extends Client> getAll();
	Client get(String id);
	boolean delete(Client client);
	boolean update(Client client);
	boolean add(Client client);
}
