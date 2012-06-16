package org.daisy.pipeline.webservice;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.daisy.pipeline.webserviceutils.XmlFormatter;
import org.daisy.pipeline.webserviceutils.XmlValidator;
import org.daisy.pipeline.webserviceutils.clients.Client;
import org.daisy.pipeline.webserviceutils.clients.SimpleClient;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ClientResource extends AdminResource {
	private Client client;

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(ClientResource.class.getName());

	@Override
    public void doInit() {
		super.doInit();
		if (!isAuthorized()) {
			return;
		}
		String idParam = (String) getRequestAttributes().get("id");
		client = webservice().getClientStore().get(idParam);
	}

    /**
     * Gets the resource.
     *
     * @return the resource
     */
    @Get("xml")
    public Representation getResource() {
    	if (!isAuthorized()) {
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return null;
    	}

    	if (client == null) {
    		setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			return null;
    	}

    	setStatus(Status.SUCCESS_OK);
		DomRepresentation dom = new DomRepresentation(MediaType.APPLICATION_XML,
				XmlFormatter.clientToXml(client, getRootRef().toString()));
		return dom;
    }

	/**
	 * Delete resource.
	 */
	@Delete
	public void deleteResource() {
		if (!isAuthorized()) {
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return;
    	}

    	if (client == null) {
    		setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			return;
    	}


		if (webservice().getClientStore().delete(client)) {
			setStatus(Status.SUCCESS_NO_CONTENT);
		}
		else {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
		}
	}

	@Put
	public Representation putResource(Representation representation) {

		if (!isAuthorized()) {
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return null;
    	}

    	// our PUT method won't create a client, just replace information for an existing client
    	if (client == null) {
    		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            return null;
    	}

        if (representation == null) {
        	// PUT request with no entity.
            setStatus(Status.CLIENT_ERROR_NOT_FOUND);
            return null;
        }

        Document doc = null;

        String s;
		try {
			s = representation.getText();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(s));
            doc = builder.parse(is);
		} catch (IOException e) {
			logger.error(e.getMessage());
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return null;
		} catch (ParserConfigurationException e) {
			logger.error(e.getMessage());
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return null;
		} catch (SAXException e) {
			logger.error(e.getMessage());
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return null;
		}


		boolean isValid = XmlValidator.validate(doc, XmlValidator.CLIENT_SCHEMA_URL);

		if (!isValid) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return null;
		}

		Element root = doc.getDocumentElement();
		// TODO add setters to the Client interface instead ?
		Client newClient = new SimpleClient(root.getAttribute("id"),root.getAttribute("secret"),Client.Role.valueOf(root.getAttribute("role")),root.getAttribute("contact"));
		webservice().getClientStore().update(newClient);

		setStatus(Status.SUCCESS_OK);
		DomRepresentation dom = new DomRepresentation(MediaType.APPLICATION_XML,
				XmlFormatter.clientToXml(newClient, getRootRef().toString()));
		return dom;

    }
}
