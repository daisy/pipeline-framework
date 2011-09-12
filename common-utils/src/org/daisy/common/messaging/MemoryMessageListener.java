package org.daisy.common.messaging;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.daisy.common.messaging.Message.Level;

import com.google.common.collect.HashMultimap;

public class MemoryMessageListener implements MessageListener,MessageAccessor {
	HashMultimap<Level, Message> mMessages = HashMultimap.create();

	private void store(Level level,String str,Throwable thw){
		Message msg= new Message.Builder().withLevel(level).withMessage(str).withThrowable(thw).build();
		mMessages.put(level, msg);
	}

	@Override
	public void trace(String msg) {
		store(Level.TRACE, msg, null);
	}

	@Override
	public void trace(String msg, Throwable throwable) {
		store(Level.TRACE, msg, throwable);
	}

	@Override
	public void debug(String msg) {
		store(Level.DEBUG, msg, null);

	}

	@Override
	public void debug(String msg, Throwable throwable) {
		store(Level.DEBUG, msg, throwable);
	}

	@Override
	public void info(String msg) {
		store(Level.INFO, msg, null);
	}

	@Override
	public void info(String msg, Throwable throwable) {
		store(Level.INFO, msg, throwable);

	}

	@Override
	public void warn(String msg) {
		store(Level.WARNING, msg, null);

	}

	@Override
	public void warn(String msg, Throwable throwable) {
		store(Level.WARNING, msg, throwable);

	}

	@Override
	public void error(String msg) {
		store(Level.ERROR, msg, null);

	}

	@Override
	public void error(String msg, Throwable throwable) {
		store(Level.ERROR, msg, throwable);
	}

	@Override
	public List<Message> getErrors() {
		return getMessagesFrom(Level.ERROR);
	}

	@Override
	public List<Message> getWarnings() {
		return getMessagesFrom(Level.WARNING);
	}

	@Override
	public List<Message> getInfos() {
		return getMessagesFrom(Level.INFO);
	}

	@Override
	public List<Message> getDebugs() {
		return getMessagesFrom(Level.DEBUG);
	}

	@Override
	public List<Message> getTraces() {
		return getMessagesFrom(Level.TRACE);
	}

	@Override
	public List<Message> getMessgages(Level... fromLevel) {
		HashSet<Level> set= new HashSet<Level>();
		set.addAll(Arrays.asList(fromLevel));
		LinkedList<Message> msgs= new LinkedList<Message>();
		for (Level iter:Level.values()){
			if(set.contains(iter)){
				msgs.addAll(mMessages.get(iter));
			}
		}
		return msgs;
	
	}
	
	private List<Message> getMessagesFrom(Level level){
		LinkedList<Message> msgs= new LinkedList<Message>();
		for (Level iter:Level.values()){
			if(iter.compareTo(level)<=0){
				msgs.addAll(mMessages.get(iter));
			}else{
				break;
			}
		}
		return msgs;
	}

	@Override
	public MessageAccessor getAccessor() {
		return this;
	}

}
