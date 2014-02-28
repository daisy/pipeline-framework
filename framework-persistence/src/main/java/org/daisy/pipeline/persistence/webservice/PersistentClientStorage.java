package org.daisy.pipeline.persistence.webservice;

import java.util.List;

import javax.persistence.NoResultException;

import org.daisy.pipeline.clients.Client;
import org.daisy.pipeline.clients.Client.Role;
import org.daisy.pipeline.clients.ClientStorage;
import org.daisy.pipeline.job.priority.Priority;
import org.daisy.pipeline.persistence.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

public class PersistentClientStorage implements ClientStorage {

    private static Logger logger = LoggerFactory
            .getLogger(PersistentClientStorage.class);

    //This client is the default client for "non client-aware" uses of the api
    final static PersistentClient DEFAULT = new PersistentClient(
            "DEFAULT_PERSISTENT_CLIENT_1685216", "", Role.ADMIN, "",
            Priority.MEDIUM);

    private Database database;

    public PersistentClientStorage(Database database) {
        this.database = database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    @Override
    public List<? extends Client> getAll() {
        return database.runQuery(String.format("select c from PersistentClient as c where c.id <>'%s'",DEFAULT.getId()),
                PersistentClient.class);
    }

    @Override
    public Optional<Client> get(String id) {
        String q = String.format(
                "select c from PersistentClient as c where c.id='%s'", id);
        try {
            return Optional.of((Client)database.getFirst(q, PersistentClient.class));
        } catch (NoResultException e) {
            return Optional.absent();
        }
    }

    @Override
    public boolean delete(Client client) {
        Optional<Client>clientInDb = get(client.getId());
        if (!clientInDb.isPresent()) {
            return false;
        }
        return database.deleteObject(clientInDb.get());
    }

    @Override
    public Optional<Client> update(Client client,String secret, Role role,
            String contactInfo, Priority priority) {
        if(client.getId().equals(DEFAULT.getId())){
            return Optional.absent();
        }
        Optional<Client>clientInDb = get(client.getId());
        if (clientInDb.isPresent()) {
            PersistentClient pClient=(PersistentClient)clientInDb.get(); 
            pClient.setContactInfo(contactInfo);
            pClient.setRole(role);
            pClient.setSecret(secret);
            database.updateObject(pClient);
            clientInDb=Optional.of((Client)pClient);
        }
        return clientInDb;
    }

    @Override
    public Optional<Client> addClient(String id, String secret, Role role,
            String contactInfo, Priority priority) {
        Optional<Client>client = get(id);
        if (client.isPresent()) {
            logger.error(String.format("ID %s is already in use.", id));
            return Optional.absent();
        }
        PersistentClient newClient= new PersistentClient(id, secret, role, contactInfo, priority);
        database.addObject(newClient);
        return Optional.of((Client)newClient);
    }

    @Override
    public Optional<Client> addClient(String id, String secret, Role role,
            String contactInfo) {
        return addClient(id, secret, role, contactInfo, Priority.MEDIUM);
    }

    @Override
    public Client defaultClient() {
        //try and get the client from the db
        Optional<Client> def = this.get(DEFAULT.getId());
        if (!def.isPresent()) {
            this.database.addObject(DEFAULT);
        }
        return DEFAULT;
    }


}
