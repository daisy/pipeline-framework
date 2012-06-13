package org.daisy.pipeline.webservice.clients;

import java.util.List;


public  interface  ClientStore<T extends Client> {
//public  interface  ClientStore {
//	List<? extends Client> getAll();
//	<T extends Client> List<T> getAll();
    List<T> getAll();
//    Iterable<Client> getAll();
	T get(String id);
	boolean delete(Client client);
	void update(Client client);
	boolean add(Client client);

}
