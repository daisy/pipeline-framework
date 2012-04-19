package org.daisy.pipeline.webservice;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.daisy.pipeline.database.DatabaseManager;
import org.daisy.pipeline.persistence.BasicDatabaseManager;
import org.daisy.pipeline.persistence.Client;
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
	private static Logger logger = LoggerFactory.getLogger(XmlFormatter.class.getName());

	@Override
    public void doInit() {
		super.doInit();
		if (!isAuthorized()) {
			return;
		}
		String idParam = (String) getRequestAttributes().get("id");
		client = Client.getClient(idParam);
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
				org.daisy.pipeline.webservice.XmlFormatter.clientToXml(client, getRootRef().toString()));
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


		if (DatabaseManager.getInstance().deleteObject(client)) {
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


		boolean isValid = Validator.validateXml(doc, Validator.clientSchema);

		if (!isValid) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return null;
		}

		Client newClient = new Client();
		Element root = doc.getDocumentElement();
		String newid = root.getAttribute("id");
		String newcontact = root.getAttribute("contact");
		String newsecret = root.getAttribute("secret");
		String newrole = root.getAttribute("role");

		client.setId(newid);
		client.setSecret(newsecret);
		client.setRole(Client.Role.valueOf(newrole));
		client.setContactInfo(newcontact);

		new BasicDatabaseManager().updateObject(client);

		setStatus(Status.SUCCESS_OK);
		DomRepresentation dom = new DomRepresentation(MediaType.APPLICATION_XML,
				XmlFormatter.clientToXml(newClient, getRootRef().toString()));
		return dom;

    }
}
