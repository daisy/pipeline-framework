package org.daisy.pipeline.webservice;

import java.net.URI;
import java.net.URISyntaxException;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.ext.xml.DomRepresentation;  
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import org.daisy.pipeline.DaisyPipelineContext;
import org.daisy.pipeline.modules.converter.ConverterDescriptor;

public class ConverterResource extends ServerResource {
	private ConverterDescriptor converterDescriptor = null;

	@Override
	public void doInit() {
		super.doInit();
		DaisyPipelineContext context = ((WebApplication)this.getApplication()).getDaisyPipelineContext();
		String converterUri = (String) getQuery().getFirstValue("id");
		System.out.println(converterUri);
		try {
			converterDescriptor = context.getConverterRegistry().getDescriptor(new URI(converterUri));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Get("xml")
	public Representation getResource() {
		if (converterDescriptor != null) {
			setStatus(Status.SUCCESS_OK);
			DomRepresentation dom = new DomRepresentation(MediaType.APPLICATION_XML, XmlFormatter.converterDescriptorToXml(converterDescriptor));
			return dom;
		}
		else {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			return null;
		}
	}
}
