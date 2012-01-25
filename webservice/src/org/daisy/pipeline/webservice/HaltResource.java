package org.daisy.pipeline.webservice;

import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;

public class HaltResource extends AuthenticatedResource {
	private PipelineWebService service;
	private long key;

	@Override
	public void doInit() {
		super.doInit();
		if (!isAuthenticated())
			return;
		service = ((PipelineWebService) this.getApplication());
		key = Long.parseLong((String) getRequestAttributes().get("key"));
	}
	@Get
	public Representation getResource() {
		if (!isAuthenticated()) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return null;
		}
		try {
			if (!service.shutDown(key)) {
				setStatus(Status.CLIENT_ERROR_FORBIDDEN);
				return null;
			}
		} catch (Exception e) {
			setStatus(Status.CONNECTOR_ERROR_INTERNAL);
			return null;
		}
		setStatus(Status.SUCCESS_OK);
		return new StringRepresentation("bye!");
	}
}
