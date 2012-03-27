package org.daisy.common.messaging.test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.daisy.common.base.Filter;
import org.daisy.common.messaging.MemoryMessage;
import org.daisy.common.messaging.Message;
import org.daisy.common.messaging.Message.Level;
import org.daisy.common.messaging.MessageAccessor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MessageAccessorTest {

	List<Message> l;
	@Before
	public void setUp(){
		l= new LinkedList<Message>();
		l.add(new MemoryMessage.Builder().withLevel(Level.INFO).withMessage("m0").withSequence(0).build());
		l.add(new MemoryMessage.Builder().withLevel(Level.DEBUG).withMessage("m1").withSequence(1).build());
		l.add(new MemoryMessage.Builder().withLevel(Level.WARNING).withMessage("m2").withSequence(2).build());
		l.add(new MemoryMessage.Builder().withLevel(Level.WARNING).withMessage("m3").withSequence(3).build());
		l.add(new MemoryMessage.Builder().withLevel(Level.DEBUG).withMessage("m4").withSequence(4).build());
	}
	@Test
	public void testFilterSequence() {
		Filter<List<Message>> filt= new MessageAccessor.SequenceFilter(2);
		List<Message> filtered= filt.filter(l);
		Assert.assertEquals(3, filtered.size());
		Assert.assertEquals("m4", filtered.get(filtered.size()-1).getMsg());
	}
	@Test
	public void testFilterLevel() {
		HashSet<Level> levels= new HashSet<Level>();
		levels.add(Level.DEBUG);
		levels.add(Level.INFO);
		Filter<List<Message>> filt= new MessageAccessor.LevelFilter(levels);
		List<Message> filtered= filt.filter(l);
		Assert.assertEquals(3, filtered.size());
		Assert.assertEquals("m4", filtered.get(filtered.size()-1).getMsg());
	}
}
