package org.daisy.pipeline.clients;

import org.daisy.pipeline.job.priority.Priority;


public interface Client {

	public enum Role {
		ADMIN, CLIENTAPP
	}

	public String getId();


	public String getSecret();


	public Role getRole();

	public String getContactInfo();

        public Priority getPriority();


}
