package org.daisy.pipeline.webservice.clients;

public final class SimpleClient implements Client {

	private final String id;
	private final String secret;
	private final Role role;
	private final String contactInfo;

	public SimpleClient(String id, String secret, Role role, String contactInfo) {
		this.id = id;
		this.secret = secret;
		this.role = role;
		this.contactInfo = contactInfo;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getSecret() {
		return secret;
	}

	@Override
	public Role getRole() {
		return role;
	}

	@Override
	public String getContactInfo() {
		return contactInfo;
	}

}
