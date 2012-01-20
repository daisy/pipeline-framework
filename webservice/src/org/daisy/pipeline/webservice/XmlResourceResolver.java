package org.daisy.pipeline.webservice;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;


public class XmlResourceResolver implements LSResourceResolver {
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(XmlResourceResolver.class.getName());
	
	@Override
	public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
		
		// schema are in the resources directory
		URL resourceUrl = XmlResourceResolver.class.getResource("resources/" + systemId);
		
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
			logger.error(e.getMessage());
			return null;
		} catch (IOException e) {
			logger.error(e.getMessage());
			return null;
		}
	}
 }
