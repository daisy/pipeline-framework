package org.daisy.pipeline.webservice;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;


public class XmlValidator {
	
	// If the Document isn't namespace-aware, this will likely fail
	public static boolean validate(Document document, URL schemaUrl) {
	    
		if (document == null || schemaUrl == null) {
			return false;
		}
		
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	    Source schemaFile;
	    InputStream is;
	    try {
			is = schemaUrl.openStream();
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
			return false;
		}
		schemaFile = new StreamSource(is);
		
	    Schema schema = null;
		try {
			schema = factory.newSchema(schemaFile);
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}

	    Validator validator = schema.newValidator();
	    
	    try {
	    	validator.validate(new DOMSource(document));
	    	try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	return true;
	    }
	    catch (SAXException e) {
	    	System.out.println("VALIDATION EXCEPTION");
	    	System.out.println(e.getMessage());
	    	try {
				is.close();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
	    	return false;
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				is.close();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			return false;
		}
	}
}
