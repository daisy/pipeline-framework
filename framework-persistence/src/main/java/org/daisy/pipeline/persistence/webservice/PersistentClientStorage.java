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

public class PersistentClientStorage implements ClientStorage {

        private static Logger logger = LoggerFactory
                        .getLogger(PersistentClientStorage.class);

        //This client is the default client for "non client-aware" uses of the api
        final static PersistentClient DEFAULT= new PersistentClient("DEFAULT_PERSISTENT_CLIENT_1685216","",Role.ADMIN,"",Priority.MEDIUM);

        private Database database;

        public PersistentClientStorage(Database database) {
                this.database = database;
        }

        public void setDatabase(Database database) {
                this.database = database;
        }

        @Override
        public List<? extends Client> getAll() {
                return database.runQuery("select c from PersistentClient as c",
                                PersistentClient.class);
        }

        @Override
        public PersistentClient get(String id) {
                String q = String.format(
                                "select c from PersistentClient as c where c.id='%s'", id);
                try {
                        //TODO check if no DB is present
                        return database.getFirst(q, PersistentClient.class);
                } catch (NoResultException e) {
                        return null;
                }
        }

        @Override
        public boolean delete(Client client) {
                PersistentClient clientInDb = get(client.getId());
                if (clientInDb == null) {
                        return false;
                }
                //TODO check if no DB is present
                return database.deleteObject(clientInDb);
        }

        @Override
        public boolean update(Client client) {
                PersistentClient clientInDb = get(client.getId());
                if (clientInDb == null) {
                        return false;
                }
                clientInDb.setContactInfo(client.getContactInfo());
                clientInDb.setRole(client.getRole());
                clientInDb.setSecret(client.getSecret());
                //TODO check if no DB is present
                database.updateObject(clientInDb);
                return true;
        }

        //@Override
        //public boolean add(Client client) {
                //Client clientExists = get(client.getId());
                //if (clientExists != null) {
                        //logger.warn(String.format("ID %s is already in use.",
                                        //client.getId()));
                        //return false;
                //}
                ////TODO check if no DB is present
                //database.addObject(new PersistentClient(client));
                //return true;
        //}

        @Override
        public Client addClient(String id, String secret, Role role,
                        String contactInfo, Priority priority) {
                Client client= get(id);
                if (client!= null) {
                        String err=String.format("ID %s is already in use.",
                                        id);
                        logger.error(err);
                        throw new RuntimeException(err);
                }
                client=new PersistentClient(id,secret,role,contactInfo,priority);
                database.addObject(client);
                return client;
        }

        @Override
        public Client addClient(String id, String secret, Role role,
                        String contactInfo) {
                return addClient(id,secret,role,contactInfo,Priority.MEDIUM);
        }

        @Override
        public Client defaultClient() {
                //try and get the client from the db
                Client def=this.get(DEFAULT.getId());
                if(def==null){
                    this.database.addObject(DEFAULT);
                    def=DEFAULT;
                }
                return def;
        }

}
