package org.daisy.pipeline.database;

//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.Id;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//// Use this class to record request nonces and timestamps
//@Entity
//public class RequestLogEntry extends BasicDatabaseObject {
//
//	/** The logger. */
//	private static Logger logger = LoggerFactory
//			.getLogger(RequestLogEntry.class.getName());
//
//	@Id
//	@GeneratedValue
//	private String internalId;
//
//	@Override
//	public String getInternalId() {
//		return internalId;
//	}
//
//	// the fields for each request
//	private String clientId;
//	private String nonce;
//	private String timestamp;
//
//	public RequestLogEntry() {
//	}
//
//	public RequestLogEntry(String clientId, String nonce, String timestamp) {
//		this.clientId = clientId;
//		this.nonce = nonce;
//		this.timestamp = timestamp;
//	}
//
//	public String getClientId() {
//		return clientId;
//	}
//
//	public void setClientId(String clientId) {
//		this.clientId = clientId;
//	}
//
//	public String getNonce() {
//		return nonce;
//	}
//
//	public void setNonce(String nonce) {
//		this.nonce = nonce;
//	}
//
//	public String getTimestamp() {
//		return timestamp;
//	}
//
//	public void setTimestamp(String timestamp) {
//		this.timestamp = timestamp;
//	}
//
//	// copy everything except the database-generated Id
//	@Override
//	public void copyData(BasicDatabaseObject object) {
//		if (!(object instanceof RequestLogEntry)) {
//			logger.warn("Could not copy data from differently-typed object.");
//			return;
//		}
//
//		RequestLogEntry entry = (RequestLogEntry) object;
//		clientId = entry.getClientId();
//		nonce = entry.getNonce();
//		timestamp = entry.getTimestamp();
//	}
//
//}