package org.daisy.pipeline.persistence;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NoResultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
// @NoSql(dataFormat=DataFormatType.MAPPED)
public class Client {

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(Client.class
			.getName());

	@Id
	@GeneratedValue
	private String internalId;

	public String getInternalId() {
		return internalId;
	}

	public enum Role {
		ADMIN, CLIENTAPP
	}

	// the fields for each client object
	private String id;
	private String secret;
	private Role role = Role.CLIENTAPP;

	// in the future, use a separate table to list contact information for
	// client app maintainers
	// with a single field, we'll just store email info
	private String contactInfo;

	public Client() {
	}

	public Client(String id, String secret, Role role, String contactInfo) {
		this.id = id;
		this.secret = secret;
		this.role = role;
		this.contactInfo = contactInfo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getContactInfo() {
		return contactInfo;
	}

	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}

	public static Iterable<Client> getAll() {
		String q = String.format("select c from Client as c");
		return new BasicDatabaseManager().runQuery(q, Client.class);
	}

	public static Client getClient(String id) {
		String q = String.format("select c from Client as c where c.id='%s'",
				id);
		try {
			return new BasicDatabaseManager().getFirst(q, Client.class);
		} catch (NoResultException e) {
			return null;
		}
	}

}
