package org.daisy.pipeline.webservice;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Resource;
import org.restlet.service.StatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thaiopensource.validate.StringOption;

public class PipelineStatusService extends StatusService{
	private static Logger logger= LoggerFactory.getLogger(PipelineStatusService.class);

	@Override
	public Representation getRepresentation(Status status, Request request,
		Response response) {
		logger.info("Overriding error representation: "+status.getThrowable().getCause().getMessage());
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		status.getThrowable().printStackTrace(new PrintStream(os));
		
		StringBuffer str=new StringBuffer();
		str.append("From: "+status.getUri());
		str.append("\n\n");
		str.append("Message: "+status.getThrowable().getCause());
		str.append("\n\nStack:\n");
		str.append(os.toString());
		return new StringRepresentation(str.toString());
		
	}

	@Override
	public Status getStatus(Throwable except, Request req, Response res) {
		
		logger.info("Error caught from application: "+except.getMessage());
		return new Status(Status.SERVER_ERROR_INTERNAL.getCode(),except,"","",req.getOriginalRef().getQuery());
	}

	@Override
	public Status getStatus(Throwable except, Resource resource) {
		
		logger.info("Error caught from application(resource): "+except.getMessage());
		return new Status(Status.SERVER_ERROR_INTERNAL.getCode(),except,"","",resource.getRequest().getOriginalRef().toString());
	}
	
}
