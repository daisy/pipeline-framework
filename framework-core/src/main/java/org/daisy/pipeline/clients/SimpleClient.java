package org.daisy.pipeline.clients;

import org.daisy.common.priority.Priority;

public final class SimpleClient implements Client {

        private final String id;
        private final String secret;
        private final Role role;
        private final String contactInfo;
        private final Priority priority;

        public SimpleClient(String id, String secret, Role role, String contactInfo) {
                this.id = id;
                this.secret = secret;
                this.role = role;
                this.contactInfo = contactInfo;
                this.priority=Priority.MEDIUM;
        }

        public SimpleClient(String id, String secret, Role role, String contactInfo,Priority priority) {
                this.id = id;
                this.secret = secret;
                this.role = role;
                this.contactInfo = contactInfo;
                this.priority=priority;
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

        @Override
        public Priority getPriority() {
                return priority;
        }

}
