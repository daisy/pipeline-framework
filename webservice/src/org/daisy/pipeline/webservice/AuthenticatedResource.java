package org.daisy.pipeline.webservice;

import org.restlet.resource.ServerResource;

public abstract class AuthenticatedResource extends ServerResource {
	
	private boolean isAuthenticated = false;
	
	@Override
	public void doInit() {
		super.doInit();
		authenticate();
	}
	
	private void authenticate() {
		isAuthenticated = Authenticator.authenticate(getQuery().getFirstValue("key"), getQuery().getFirstValue("sign"),
				getQuery().getFirstValue("time"), getQuery().getFirstValue("nonce"), getReference().toString());
	}
	
	public boolean isAuthenticated() {
		return isAuthenticated;
	}
	
}