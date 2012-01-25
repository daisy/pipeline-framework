package org.daisy.pipeline.database;

//dummy class to mimic the eventual "real" persistence db version
public class Client extends BasicDatabaseObject{

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

	// in the future, use a separate table to list contact information for client app maintainers
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


}
