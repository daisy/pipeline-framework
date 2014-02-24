package org.daisy.pipeline.nonpersistent.webservice;

import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.daisy.pipeline.clients.Client;
import org.daisy.pipeline.clients.SimpleClient;
import org.daisy.pipeline.nonpersistent.webservice.VolatileClientStorage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class VolatileClientStorageTest   {

	Client c1;
	Client c2;
	VolatileClientStorage clients;	
	@Before
	public void setUp(){
		c1=new SimpleClient("1","supersecret",Client.Role.CLIENTAPP,"pipeliners@daisy.org");
		c2=new SimpleClient("2","notthatsecret",Client.Role.ADMIN,"pipeliners@daisy.org");
		clients=new VolatileClientStorage();
		clients.add(c1);
		clients.add(c2);

	}

	@Test
	public void addPresent() {
		Assert.assertFalse(clients.add(c1));
	}

	@Test
	public void get() {
		Client res=clients.get("1");
		Assert.assertEquals("1",res.getId());
	}

	@Test
	public void getEmpty() {
		Client res=clients.get("10000");
		Assert.assertNull(res);
	}

	@Test
	public void delete() {
		//assert the delete
		Assert.assertTrue(clients.delete(c1));
		Client res=clients.get("1");
		//and then that it's not present
		Assert.assertNull(res);
	}

	@Test
	public void deleteNotPresent() {
		clients.delete(c1);
		Assert.assertFalse(clients.delete(c1));
	}

	@Test
	public void getAll() {
		List<? extends Client> list=clients.getAll();
		Assert.assertEquals(list.size(),2);
		Set<String> ids=new HashSet<String>();
		for( Client c : list){
			ids.add(c.getId());
		}
		Assert.assertTrue(ids.contains("1"));
		Assert.assertTrue(ids.contains("2"));
	}

	@Test
	public void update() {
		Client other=new SimpleClient(c1.getId(),"X",c1.getRole(),c1.getContactInfo());
		clients.update(other);
		Assert.assertEquals(clients.get(c1.getId()).getSecret(),"X");
	}

	@Test
	public void updateNotPresent() {
		Client other=new SimpleClient("1000","X",c1.getRole(),c1.getContactInfo());
		Assert.assertFalse(clients.update(other));
	}

}
