package org.daisy.pipeline.persistence.webservice;

import java.util.List;

import org.daisy.pipeline.clients.Client;
import org.daisy.pipeline.clients.Client.Role;
import org.daisy.pipeline.job.priority.Priority;
import org.daisy.pipeline.persistence.Database;
import org.daisy.pipeline.persistence.jobs.DatabaseProvider;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;


@RunWith(MockitoJUnitRunner.class)
public class PersistentClientStorageTest {

    Database db;
    PersistentClientStorage storage;
    String secret="secret";
    String contact="name@server.com";
    List<Client> toDel;
    @Before
    public void setUp(){
        db=Mockito.spy(DatabaseProvider.getDatabase());
        storage=new PersistentClientStorage(db);
        toDel= Lists.newLinkedList();
    }

    @After
    public void tearDown(){
        for(Client c:toDel){
            db.deleteObject(c);
        }
    }


    @Test
    public void listWithDefault(){
        Client c= storage.defaultClient();
        toDel.add(c);
        Assert.assertEquals("Default should not be in the list",0,storage.getAll().size());

    }

    @Test
    public void addClient(){
        String id="cli"; 
        Optional<Client> c=storage.addClient(id,secret,Role.ADMIN,contact,Priority.HIGH);
        Assert.assertTrue(c.isPresent());
        toDel.add(c.get());
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
        Optional<Client>def=storage.update(PersistentClientStorage.DEFAULT,"",Role.ADMIN,"",Priority.LOW);
        //make sure is in the db
        toDel.add(c);
        Assert.assertFalse(def.isPresent());
    }
    @Test
    public void addClientDefaultPriority(){
        String id="cli"; 
        Optional<Client>c =storage.addClient(id,secret,Role.ADMIN,contact,Priority.MEDIUM);
        toDel.add(c.get());
        Optional<Client> res=storage.get(id);
        Assert.assertTrue(res.isPresent());
        Assert.assertEquals("priority",Priority.MEDIUM,res.get().getPriority());
    }

    @Test
    public void getDefaultClientEmpty(){
        Client def= storage.defaultClient();
        toDel.add(def);
        //the client has been inserted in the db
        Mockito.verify(db,Mockito.times(1)).addObject(Mockito.any());
        Assert.assertEquals("Check default id",PersistentClientStorage.DEFAULT.getId(),def.getId());
    }

    @Test
    public void getDefaultClientTwice(){
        Client def= storage.defaultClient();
        toDel.add(def);
        //the client has been inserted in the db but only once
        Mockito.verify(db,Mockito.times(1)).addObject(Mockito.any());
        def= storage.defaultClient();
        Mockito.verify(db,Mockito.times(1)).addObject(Mockito.any());
        Assert.assertEquals("Check default id",PersistentClientStorage.DEFAULT.getId(),def.getId());
    }
}
