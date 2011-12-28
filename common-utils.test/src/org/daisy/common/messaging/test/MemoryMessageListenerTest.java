package org.daisy.common.messaging.test;

import java.util.HashSet;
import java.util.List;

import org.daisy.common.base.Filter;
import org.daisy.common.messaging.MemoryMessageListener;
import org.daisy.common.messaging.Message;
import org.daisy.common.messaging.MessageAccessor;
import org.daisy.common.messaging.Message.Level;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MemoryMessageListenerTest {
	MemoryMessageListener accessor;
	
	@Before
	public void setUp(){
		accessor=new MemoryMessageListener();
		accessor.info("m0");
		accessor.debug("m1");
		accessor.warn("m2");
		accessor.warn("m3");
		accessor.debug("m4");
	}
	
	@Test
	public void applyFiltersTest() {
		HashSet<Level> levels= new HashSet<Level>();
		levels.add(Level.DEBUG);
		
		Filter<List<Message>> seqFilt= new MessageAccessor.SequenceFilter(2);
		Filter<List<Message>> levelFilt= new MessageAccessor.LevelFilter(levels);
		Filter[] arr = new Filter[]{seqFilt,levelFilt};
		List<Message> filtered = accessor.filtered(arr);
		Assert.assertEquals(1, filtered.size());
		Assert.assertEquals("m4", filtered.get(filtered.size()-1).getMsg());

	}

}
