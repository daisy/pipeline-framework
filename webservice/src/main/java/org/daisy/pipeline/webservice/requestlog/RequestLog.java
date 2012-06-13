package org.daisy.pipeline.webservice.requestlog;

public interface RequestLog {
	boolean contains(RequestLogEntry entry);
	void add(RequestLogEntry entry);
}
