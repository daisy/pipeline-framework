package org.daisy.pipeline.webservice;

import org.daisy.pipeline.webserviceutils.Authenticator;
import org.daisy.pipeline.webserviceutils.clients.Client;
import org.daisy.pipeline.webserviceutils.requestlog.RequestLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AuthenticatedResource extends GenericResource {
	private static Logger logger = LoggerFactory.getLogger(Authenticator.class.getName());
	private boolean isAuthenticated = false;

	@Override
	public void doInit() {
		super.doInit();
		if (webservice().getConfiguration().isAuthenticationEnabled() == false) {
			// if authentication is not enabled, then all requests can be considered automatically authenticated
			isAuthenticated = true;
		}
		else {
			isAuthenticated = authenticate();
		}
	}

	private boolean authenticate() {

		long maxRequestTime = webservice().getConfiguration().getMaxRequestTime();
		String authid = getQuery().getFirstValue("authid");
		Client client = webservice().getClientStore().get(authid);
		// make sure the client exists
		if (client == null) {
			logger.error(String.format("Client with auth ID %s not found", authid));
			return false;
		}
		RequestLog requestLog = webservice().getRequestLog();
		return new Authenticator(requestLog).authenticate(client, getQuery().getFirstValue("sign"),
				getQuery().getFirstValue("time"), getQuery().getFirstValue("nonce"), getReference().toString(),
				maxRequestTime);
	}

	public boolean isAuthenticated() {
		return isAuthenticated;
	}

}
