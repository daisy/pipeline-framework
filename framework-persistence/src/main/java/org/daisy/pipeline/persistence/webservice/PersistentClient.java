package org.daisy.pipeline.persistence.webservice;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.daisy.pipeline.webserviceutils.clients.Client;

@Entity
// @NoSql(dataFormat=DataFormatType.MAPPED)
public class PersistentClient implements Client {

	@Id
	@GeneratedValue
	private String internalId;

	public String getInternalId() {
		return internalId;
	}

	public enum PersistentRole {
		ADMIN, CLIENTAPP
	}

	// the fields for each client object
	private String id;
	private String secret;
	private PersistentRole role = PersistentRole.CLIENTAPP;

	// in the future, use a separate table to list contact information for
	// client app maintainers
	// with a single field, we'll just store email info
	private String contactInfo;

	public PersistentClient() {
	}

	public PersistentClient(Client client) {
		this.id = client.getId();
		this.secret = client.getSecret();
		this.role = PersistentRole.valueOf(client.getRole().name());
		this.contactInfo = client.getContactInfo();
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
		return Role.valueOf(role.name());
	}

	public void setRole(Role role) {
		this.role = PersistentRole.valueOf(role.name());
	}

	public String getContactInfo() {
		return contactInfo;
	}

	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}

}
