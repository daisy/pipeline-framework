package org.daisy.pipeline.webservice.restlet.impl;

import java.util.ArrayList;

import org.daisy.common.properties.Properties;
import org.daisy.common.properties.Properties.SettableProperty;
import org.daisy.pipeline.webservice.restlet.AdminResource;
import org.daisy.pipeline.webservice.xml.PropertiesXmlWriter;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

public class PropertiesResource extends AdminResource {

	@Override
	public void doInit() {
		super.doInit();
		if (!isAuthorized()) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return;
		}
	}

	@Get("xml")
	public Representation getResource() {
		logRequest();
		maybeEnableCORS();
		if (!isAuthorized()) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return null;
		}
		PropertiesXmlWriter writer = new PropertiesXmlWriter(new ArrayList<>(Properties.getSettableProperties()),
		                                                     getRequest().getRootRef().toString());
		DomRepresentation dom = new DomRepresentation(MediaType.APPLICATION_XML, writer.getXmlDocument());
		setStatus(Status.SUCCESS_OK);
		logResponse(dom);
		return dom;
	}
}
