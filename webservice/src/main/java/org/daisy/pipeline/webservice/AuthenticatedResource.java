package org.daisy.pipeline.webservice;


public abstract class AuthenticatedResource extends GenericResource {

	private boolean isAuthenticated = false;

	@Override
	public void doInit() {
		super.doInit();
		if (webservice().isAuthenticationEnabled() == false) {
			// if authentication is not enabled, then all requests can be considered automatically authenticated
			isAuthenticated = true;
		}
		else {
			isAuthenticated = authenticate();
		}
	}

	private boolean authenticate() {

		long maxRequestTime = webservice().getMaxRequestTime();
		return Authenticator.authenticate(webservice(),getQuery().getFirstValue("authid"), getQuery().getFirstValue("sign"),
				getQuery().getFirstValue("time"), getQuery().getFirstValue("nonce"), getReference().toString(),
				maxRequestTime);
	}

	public boolean isAuthenticated() {
		return isAuthenticated;
	}

}