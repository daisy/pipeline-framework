package org.daisy.common.messaging;

import java.util.List;

import org.daisy.common.messaging.Message.Level;

public interface MessageAccessor{
  
	public List<Message> getErrors();
	public List<Message> getWarnings();
	public List<Message> getInfos();
	public List<Message> getDebugs();
	public List<Message> getTraces();
	public List<Message> getMessgages(Level... fromLevels);
}
