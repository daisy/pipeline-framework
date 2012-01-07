package org.daisy.pipeline.webservice;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;


public class SchemaResourceResolver implements LSResourceResolver {
	@Override
	public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
		
		// schema are in the resources directory
		URL resourceUrl = SchemaResourceResolver.class.getResource("resources/" + systemId);
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			DOMImplementation di = db.getDOMImplementation();
	        DOMImplementationLS ls = (DOMImplementationLS) di;
	        LSInput lsi = ls.createLSInput();
	        InputStream is = resourceUrl.openStream();
	        lsi.setByteStream(is);
			return lsi;
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
 }
