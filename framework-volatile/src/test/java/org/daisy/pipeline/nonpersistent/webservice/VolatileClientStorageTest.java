package org.daisy.pipeline.nonpersistent.webservice;

import org.daisy.pipeline.clients.Client;
import org.daisy.pipeline.clients.Client.Role;
import org.daisy.pipeline.job.priority.Priority;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Optional;

public class VolatileClientStorageTest   {

        VolatileClientStorage storage;  
        String secret="secret";
        String contact="name@server.com";
        @Before
        public void setUp(){
                storage=new VolatileClientStorage();

        }

        @Test
        public void addPresent() {
                String id="cli"; 
                Optional<Client> c=storage.addClient(id,secret,Role.ADMIN,contact,Priority.HIGH);
                Assert.assertTrue(c.isPresent());
                Client res=storage.get(id).get();
                Assert.assertEquals("id",id,res.getId());
                Assert.assertEquals("secret",secret,res.getSecret());
                Assert.assertEquals("contact",contact,res.getContactInfo());
                Assert.assertEquals("role",Role.ADMIN,res.getRole());
                Assert.assertEquals("priority",Priority.HIGH,res.getPriority());
        }

        @Test 
        public void addDefault(){
                Client c= storage.defaultClient();
                Optional<Client>def=storage.update(VolatileClientStorage.DEFAULT,"",Role.ADMIN,"",Priority.LOW);
                Assert.assertFalse(def.isPresent());
        }

        @Test
        public void getEmpty() {
                Optional<Client> res=storage.get("10000");
                Assert.assertFalse(res.isPresent());
        }

        @Test
        public void delete() {
                String id="cli"; 
                Optional<Client> c=storage.addClient(id,secret,Role.ADMIN,contact,Priority.HIGH);
                //assert the delete
                Assert.assertTrue(storage.delete(c.get()));
                Optional<Client> res=storage.get("1");
                //and then that it's not present
                Assert.assertFalse(res.isPresent());
        }

        @Test
        public void deleteNotPresent() {
                Client c=new VolatileClient("id","",Role.ADMIN,"",Priority.LOW);
                Assert.assertFalse(storage.delete(c));
        }


        @Test
        public void update() {
                Client cli=storage.addClient("id",secret,Role.ADMIN,contact,Priority.LOW).get();
                Optional<Client> updated=storage.update(cli,"mytreasure",Role.CLIENTAPP,"paco@gmail.com",Priority.HIGH);

                Assert.assertEquals("mytreasure",updated.get().getSecret());
                Assert.assertEquals("paco@gmail.com",updated.get().getContactInfo());
                Assert.assertEquals(Role.CLIENTAPP,updated.get().getRole());
                Assert.assertEquals(Priority.HIGH,updated.get().getPriority());

        }


}
