package org.daisy.pipeline.webservice.requestlog;

public interface RequestLogEntry {

	String getClientId();
	String getNonce();
	String getTimestamp();
}
