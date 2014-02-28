package org.daisy.pipeline.nonpersistent.webservice;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.daisy.pipeline.clients.Client;
import org.daisy.pipeline.clients.Client.Role;
import org.daisy.pipeline.clients.ClientStorage;
import org.daisy.pipeline.job.priority.Priority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

public class VolatileClientStorage implements ClientStorage {

        static final VolatileClient DEFAULT = new VolatileClient(
                        "DEFAULT_VOLATILE_CLIENT_7333298", "", Role.ADMIN, "",
                        Priority.MEDIUM);
        private static Logger logger = LoggerFactory
                .getLogger(VolatileClientStorage.class);
        private Map<String, Client> clients;

        public VolatileClientStorage() {
                this.clients = Collections
                        .synchronizedMap(new HashMap<String, Client>());
        }

        @Override
        public List<? extends Client> getAll() {
                return Collections.unmodifiableList(new LinkedList<Client>(this.clients
                                        .values()));
        }

        @Override
        public Optional<Client> get(String id) {
                //is the default
                if(DEFAULT.getId().equals(id)){
                        return Optional.of((Client)DEFAULT);
                }
                //otherwise
                Client c=this.clients.get(id);
                return Optional.fromNullable(c);
        }

        @Override
        public boolean delete(Client client) {
                return this.clients.remove(client.getId()) != null;
        }


        private Client add(Client client) {
                if (client.getId().equals(DEFAULT.getId())||this.clients.containsKey(client.getId())) {
                        return null;
                } else {
                        this.clients.put(client.getId(), client);
                        return client;
                }
        }

        @Override
        public Optional<Client> addClient(String id, String secret, Role role,
                        String contactInfo, Priority priority) {
                Client cli=new VolatileClient(id,secret,role,contactInfo,priority);
                return Optional.fromNullable(this.add(cli));
        }

        @Override
        public Optional<Client> addClient(String id, String secret, Role role,
                        String contactInfo) {

                return this.addClient(id,secret,role,contactInfo,Priority.MEDIUM);
        }

        @Override
        public Client defaultClient() {
                return DEFAULT;
        }

        @Override
        public Optional<Client> update(Client client, String secret, Role role,
                        String contactInfo, Priority priority) {
                if(client.getId().equals(DEFAULT.getId())){
                        return Optional.absent();
                }

                Optional<Client> toUpdate = this.get(client.getId());
                if ( toUpdate.isPresent()) {
                        VolatileClient vol=(VolatileClient)toUpdate.get();
                        vol.setSecret(secret);
                        vol.setRole(role);
                        vol.setContactInfo(contactInfo);
                        vol.setPriority(priority);
                } 
                return toUpdate;

        }

}
