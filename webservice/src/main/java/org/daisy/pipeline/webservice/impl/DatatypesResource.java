package org.daisy.pipeline.webservice.impl;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.daisy.pipeline.datatypes.DatatypeService;
import org.daisy.pipeline.webserviceutils.xml.ClientXmlWriter;
import org.daisy.pipeline.webserviceutils.xml.XmlWriterFactory;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.google.common.base.Optional;

public class DatatypesResource extends AdminResource {


        private static final Logger logger = LoggerFactory.getLogger(DatatypeResource.class);
        @Override
        public void doInit() {
                super.doInit();
                if (!isAuthorized()) {
                        return;
                }
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
                DomRepresentation dom;
                Iterable<DatatypeService> datatypes = webservice().getDatatypeRegistry().getDatatypes();
                try {
                        dom = new DomRepresentation(MediaType.APPLICATION_XML, 
                                        XmlWriterFactory.
                                        createXmlWriterForDatatypes(datatypes).getXmlDocument());
                } catch (Exception e) {

                        setStatus(Status.SERVER_ERROR_INTERNAL);
                        return this.getErrorRepresentation(e.getMessage());       
                }
                return dom;
        }

}
