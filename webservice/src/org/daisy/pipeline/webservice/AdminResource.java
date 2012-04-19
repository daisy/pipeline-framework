package org.daisy.pipeline.webservice;

import org.daisy.pipeline.persistence.Client;


public class AdminResource extends AuthenticatedResource {
	private boolean isAuthorized = false;

	@Override
	public void doInit() {
		super.doInit();
		if (((PipelineWebService)getApplication()).isAuthenticationEnabled() == false) {
			// if authentication is not enabled, then all requests can be considered automatically authorized
			isAuthorized = true;
		}
		else {

			isAuthorized = super.isAuthenticated() && authorizeAsAdmin();
		}
	}

	private boolean authorizeAsAdmin() {

		String id = getQuery().getFirstValue("authid");

		Client client = Client.getClient(id);
		if (client == null) {
			return false;
		}
		if (client.getRole() == Client.Role.ADMIN) {
			return true;
		}
		return false;
	}

	public boolean isAuthorized() {
		return isAuthorized;
	}


}
