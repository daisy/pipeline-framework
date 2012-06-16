package org.daisy.pipeline.push;

import java.net.URI;

import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.webserviceutils.Authenticator;
import org.daisy.pipeline.webserviceutils.XmlFormatter;
import org.daisy.pipeline.webserviceutils.callback.Callback;
import org.daisy.pipeline.webserviceutils.clients.Client;
import org.restlet.resource.ClientResource;
import org.w3c.dom.Document;

public class Poster {

	// TODO how to get the base URI ahead of time?  It would be good to have it available in webserviceutils.
	// However, WebService itself may not know what it is until a Resource is requested.
	// we could go back to having it in a config file somewhere..

	public static void postMessage(Job job, int msgSeq, Callback callback) {
		URI url = callback.getHref();
		// TODO use the right base URI
		Document doc = XmlFormatter.jobToXml(job, msgSeq, "http://localhost:8181", true, false);
		postXml(doc, url, callback.getClient());

	}

	public static void postStatusUpdate(Job job, Callback callback) {
		URI url = callback.getHref();
		// TODO use the right base URI
		Document doc = XmlFormatter.jobToXml(job, 0, "http://localhost:8181", false, false);
		postXml(doc, url, callback.getClient());
	}

	private static void postXml(Document doc, URI url, Client client) {
		URI requestUri = url;
		if (client != null) {
			requestUri = Authenticator.createUriWithCredentials(url.toString(), client);
		}
		// use restlet ClientResource with the callback URI
		ClientResource resource = new ClientResource(requestUri.toString());
		resource.post(doc);
	}
}