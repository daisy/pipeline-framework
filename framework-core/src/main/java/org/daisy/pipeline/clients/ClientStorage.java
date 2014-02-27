package org.daisy.pipeline.clients;

import java.util.List;

import org.daisy.pipeline.clients.Client.Role;
import org.daisy.pipeline.job.priority.Priority;

public interface  ClientStorage {
    public List<? extends Client> getAll();
    public Client get(String id);
    public boolean delete(Client client);
    public boolean update(Client client);
    public Client addClient(String id, String secret, Role role, String contactInfo,Priority priority);
    public Client addClient(String id, String secret, Role role, String contactInfo);
    public Client defaultClient();
}
