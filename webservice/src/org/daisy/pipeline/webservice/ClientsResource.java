package org.daisy.pipeline.webservice;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.daisy.pipeline.persistence.webservice.Client;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ClientsResource extends AdminResource {

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(XmlFormatter.class.getName());

	@Override
    public void doInit() {
		super.doInit();
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

    	setStatus(Status.SUCCESS_OK);
		DomRepresentation dom = new DomRepresentation(MediaType.APPLICATION_XML,
				XmlFormatter.clientsToXml(Client.getAll(),
						getRootRef().toString()));

		return dom;
    }

    @Post
    public Representation createResource(Representation representation) {
    	if (!isAuthorized()) {
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return null;
    	}

        if (representation == null) {
        	// POST request with no entity.
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
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

		Element root = doc.getDocumentElement();
		String newid = root.getAttribute("id");
		String newcontact = root.getAttribute("contact");
		String newsecret = root.getAttribute("secret");
		String newrole = root.getAttribute("role");

		Client newClient = new Client();
		newClient.setContactInfo(newcontact);
		newClient.setId(newid);
		newClient.setRole(Client.Role.valueOf(newrole));
		newClient.setSecret(newsecret);

		if (!DatabaseHelper.getInstance().addClient(newClient)) {
			// the client ID was probably not unique
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return null;
		}

		setStatus(Status.SUCCESS_CREATED);
		DomRepresentation dom = new DomRepresentation(MediaType.APPLICATION_XML, XmlFormatter.clientToXml(newClient, getRootRef().toString()));
		return dom;
    }
}
