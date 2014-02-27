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
    public void addClient(){
        String id="cli"; 
        Client c=storage.addClient(id,secret,Role.ADMIN,contact,Priority.HIGH);
        toDel.add(c);
        Client res=storage.get(id);
        Assert.assertNotNull(res);
        Assert.assertEquals("id",id,res.getId());
        Assert.assertEquals("secret",secret,res.getSecret());
        Assert.assertEquals("contact",contact,res.getContactInfo());
        Assert.assertEquals("role",Role.ADMIN,res.getRole());
        Assert.assertEquals("priority",Priority.HIGH,res.getPriority());
    }

    @Test
    public void addClientDefaultPriority(){
        String id="cli"; 
        Client c=storage.addClient(id,secret,Role.ADMIN,contact,Priority.MEDIUM);
        toDel.add(c);
        Client res=storage.get(id);
        Assert.assertNotNull(res);
        Assert.assertEquals("priority",Priority.MEDIUM,res.getPriority());
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
