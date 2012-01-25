package org.daisy.pipeline.database;

//dummy class to mimic the eventual "real" persistence db version
public class RequestLogEntry extends BasicDatabaseObject {

	private final String clientId;
	private final String nonce;
	private final String timestamp;

	public RequestLogEntry(String clientId, String nonce, String timestamp) {
		this.clientId = clientId;
		this.nonce = nonce;
		this.timestamp = timestamp;
	}

}
