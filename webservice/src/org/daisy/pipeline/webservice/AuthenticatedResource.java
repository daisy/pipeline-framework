package org.daisy.pipeline.webservice;

import org.restlet.resource.ServerResource;

public abstract class AuthenticatedResource extends ServerResource {

	private boolean isAuthenticated = false;

	@Override
	public void doInit() {
		super.doInit();
		if (((PipelineWebService)getApplication()).isAuthenticationEnabled() == false) {
			// if authentication is not enabled, then all requests can be considered automatically authenticated
			isAuthenticated = true;
		}
		else {
			isAuthenticated = authenticate();
		}
	}

	private boolean authenticate() {

		long maxRequestTime = ((PipelineWebService)getApplication()).getMaxRequestTime();
		return Authenticator.authenticate(getQuery().getFirstValue("id"), getQuery().getFirstValue("sign"),
				getQuery().getFirstValue("time"), getQuery().getFirstValue("nonce"), getReference().toString(),
				maxRequestTime);
	}

	public boolean isAuthenticated() {
		return isAuthenticated;
	}

}