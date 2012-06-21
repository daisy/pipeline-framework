package org.daisy.pipeline.push;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URI;

import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.webserviceutils.Authenticator;
import org.daisy.pipeline.webserviceutils.XmlFormatter;
import org.daisy.pipeline.webserviceutils.callback.Callback;
import org.daisy.pipeline.webserviceutils.clients.Client;
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

		// from http://code.geek.sh/2009/10/simple-post-in-java/
		HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) requestUri.toURL().openConnection();
        } catch (IOException e) {
        	// TODO Auto-generated catch block
			e.printStackTrace();
        }

        connection.setDoInput (true);
        connection.setDoOutput (true); // TODO do we need this one too?
        connection.setUseCaches (false);

        try {
            connection.setRequestMethod("POST");
        } catch (ProtocolException e) {
        	// TODO Auto-generated catch block
			e.printStackTrace();
        }

        try {
            connection.connect();
        } catch (IOException e) {
        	// TODO Auto-generated catch block
			e.printStackTrace();
        }

        DataOutputStream output = null;
        DataInputStream input = null;

        try {
            output = new DataOutputStream(connection.getOutputStream());
        } catch (IOException e) {
        	// TODO Auto-generated catch block
			e.printStackTrace();
        }

        // Send the request data.
        try {
            output.writeBytes(XmlFormatter.DOMToString(doc));
            output.flush();
            output.close();
        } catch (IOException e) {
        	// TODO Auto-generated catch block
			e.printStackTrace();
        }

        // Get response data.
        String str = null;
        try {
            input = new DataInputStream (connection.getInputStream());
            while (null != ((str = input.readLine()))) {
            //    System.out.println(str);
            }
            input.close ();
        } catch (IOException e) {
        	// TODO Auto-generated catch block
			e.printStackTrace();
        }
	}


}