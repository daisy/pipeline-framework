package org.daisy.pipeline.webservice;

import org.daisy.pipeline.webservice.clients.Client;



public class AdminResource extends AuthenticatedResource {
	private boolean isAuthorized = false;

	@Override
	public void doInit() {
		super.doInit();
		// Note: if authentication is not enabled, then all requests can be considered automatically authorized
		isAuthorized = (!webservice().isAuthenticationEnabled()) || (super.isAuthenticated() && authorizeAsAdmin());
	}

	private boolean authorizeAsAdmin() {
		String id = getQuery().getFirstValue("authid");
		Client client = webservice().getClientStore().get(id);
		return client!=null && Client.Role.ADMIN.equals(client.getRole());
	}

	public boolean isAuthorized() {
		return isAuthorized;
	}


}
