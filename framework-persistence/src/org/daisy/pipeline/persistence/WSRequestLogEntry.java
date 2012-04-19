package org.daisy.pipeline.persistence;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.eclipse.persistence.annotations.DataFormatType;
import org.eclipse.persistence.annotations.NoSql;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Use this class to record request nonces and timestamps
@Entity
@NoSql(dataFormat=DataFormatType.MAPPED)
public class WSRequestLogEntry {

	/** The logger. */
	private static Logger logger = LoggerFactory
			.getLogger(WSRequestLogEntry.class.getName());

	@Id
	@GeneratedValue
	private String internalId;

	public String getInternalId() {
		return internalId;
	}

	// the fields for each request
	private String clientId;
	private String nonce;
	private String timestamp;

	public WSRequestLogEntry() {
	}

	public WSRequestLogEntry(String clientId, String nonce, String timestamp) {
		this.clientId = clientId;
		this.nonce = nonce;
		this.timestamp = timestamp;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getNonce() {
		return nonce;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}	
}