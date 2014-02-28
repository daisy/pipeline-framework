package org.daisy.pipeline.webservice;

import org.daisy.pipeline.clients.Client;

import com.google.common.base.Optional;

public class AdminResource extends AuthenticatedResource {
        private boolean isAuthorized = false;

        @Override
        public void doInit() {
                super.doInit();
                // Note: if authentication is not enabled, then all requests can be considered automatically authorized
                isAuthorized = (!webservice().getConfiguration()
                                .isAuthenticationEnabled())
                                || (super.isAuthenticated() && authorizeAsAdmin());
        }

        private boolean authorizeAsAdmin() {
                String id = getQuery().getFirstValue("authid");
                Optional<Client> client = webservice().getStorage().getClientStorage().get(id);
                return client.isPresent();
        }

        public boolean isAuthorized() {
                return isAuthorized;
        }


}
