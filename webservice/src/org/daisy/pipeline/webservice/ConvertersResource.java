package org.daisy.pipeline.webservice;

import org.daisy.pipeline.DaisyPipelineContext;
import org.daisy.pipeline.modules.converter.ConverterDescriptor;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class ConvertersResource extends ServerResource {
	Iterable<ConverterDescriptor> converterDescriptors;
	
	@Override
	public void doInit() {
		super.doInit();
		DaisyPipelineContext context = ((WebApplication)this.getApplication()).getDaisyPipelineContext();
		converterDescriptors = context.getConverterRegistry().getDescriptors();

	}
	
	@Get("xml")
	public Representation getResource() {
		setStatus(Status.SUCCESS_OK);
		DomRepresentation dom = new DomRepresentation(MediaType.APPLICATION_XML, XmlFormatter.converterDescriptorsToXml(converterDescriptors));
		return dom;
		
	}
}
