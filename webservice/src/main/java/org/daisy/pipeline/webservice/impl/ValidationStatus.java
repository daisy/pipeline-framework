package org.daisy.pipeline.webservice.impl;

public class ValidationStatus {

        private boolean valid;
        private String message;

        /**
         * @param valid
         * @param message
         */
        public ValidationStatus(boolean valid, String message) {
                this.valid = valid;
                this.message = message;
        }
        /**
         * @param valid
         * @param message
         */
        public ValidationStatus(boolean valid) {
                this.valid = valid;
        }

        /**
         * @return the valid
         */
        public boolean isValid() {
                return valid;
        }

        /**
         * @return the message
         */
        public String getMessage() {
                return message;
        }
        
}
