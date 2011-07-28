/*
 * Copyright (C) 2010 Dedicon <http://dedicon.nl>

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

Linking altText statically or dynamically with other modules
is making a combined work based on altText. Thus, the terms and
conditions of the GNU General Public License cover
the whole combination.  In addition, as a special exception,
the copyright holders of altText give you permission to
combine altText program with free software programs or libraries
that are released under the GNU LGPL or the Common Public License version 1.0.
You may copy and distribute such a system following the terms
of the GNU GPL for altText and the licenses of the other code
concerned, provided that you include the source code of that other
code when and as the GNU GPL requires distribution of source code.

Note that people who make modified versions of altText are not obligated to
grant this special exception for their modified versions; it is their
choice whether to do so. The GNU General Public License gives permission
to release a modified version without this exception; this exception
also makes it possible to release a modified version which carries
forward this exception.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

Author : Javier Asensio Cubero capitan{.}cambio{@}gmail{.}com
 */
package org.daisy.pipeline.modules.converter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

// TODO: Auto-generated Javadoc
/**
 * The Class XmlDocument is a wrapper for annoying xml java api.
 *
 * @author javier
 * @version 1.0
 * @created 21-okt-2009 13:52:59
 */
public class XmlDocument {

        /** The dom doc. */
        private Document mDoc;

        /** The m err handler. */
        private ErrorHandler mErrHandler;

        /** stores the document validation. */
        private boolean mValid = true;

        /** stores if this document has to be validated. */
        private boolean mValidate = true;

        /** The m namespace. */
        private boolean mNamespace = true;

        /** The m ns context. */
        private NamespaceContext mNSContext = null;

        /** The xpath cache. */
        private static XpathCache mCache = null;

        /** The dummy resolver. In case resolving elements is not necessary or impossible */
        EntityResolver dummyResolver = null;

        /** The xml's doctype. */
        private DocumentType mDocType;

        /** The logger. */
        private Logger logger = Logger.getLogger("nl.dedicon.converter.core.xml");

        /**
         * Instantiates a new xml document.
         */
        public XmlDocument() {
                this.logger.entering(this.getClass().getName(), this.getClass()
                                .getName());
                mDoc = null;
                // Error hander for updating the mValid attrib, ignore warnings and
                // throwing errors
                mErrHandler = new ErrorHandler() {

                        @Override
                        public void warning(SAXParseException arg0) throws SAXException {
                                // ignore warnings
                                return;
                        }

                        @Override
                        public void fatalError(SAXParseException arg0) throws SAXException {
                                // hot potato
                                throw arg0;

                        }

                        @Override
                        public void error(SAXParseException arg0) throws SAXException {
                                // validation failed
                                logger.info("The xml file is not valid:" + arg0.getMessage());
                                mValid = false;
                        }
                };

                // kind of XpathCache singleton
                if (this.mCache == null) {
                        this.mCache = new XpathCache();
                }

        }

        /**
         * Instantiates a new xml document setting the document type.
         *
         * @param docType the doc type
         */
        public XmlDocument(DocumentType docType) {
                this();
                this.mDocType = docType;
        }

        /**
         * Load the xml from a file.
         *
         * @param path the path
         *
         * @throws IOException Signals that an I/O exception has occurred.
         * @throws SAXException the SAX exception
         * @throws ParserConfigurationException the parser configuration exception
         */
        public void loadFromFile(String path) throws java.io.IOException,
                        org.xml.sax.SAXException, ParserConfigurationException {

                File file = new File(path);

                FileInputStream fis = new FileInputStream(file);
                this.loadFromInputStream(fis);
               // fis.getChannel().close();
               // logger.fine("FILE IS :"+fis.getChannel().isOpen());
                fis.close();
               
               
                this.logger.info("Xml file " + path + " has been loaded successffully");
        }

        /**
         * Load the xml from an input stream.
         *
         * @param is the is
         *
         * @throws IOException Signals that an I/O exception has occurred.
         * @throws SAXException the SAX exception
         * @throws ParserConfigurationException the parser configuration exception
         */
        public void loadFromInputStream(InputStream is) throws java.io.IOException,
                        org.xml.sax.SAXException, ParserConfigurationException {
                this.logger.entering("XmlDocument", "loadFromFile");
                try {

                        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                        dbf.setValidating(mValidate);
                        dbf.setNamespaceAware(mNamespace);
                        DocumentBuilder db = dbf.newDocumentBuilder();
                        db.setErrorHandler(mErrHandler);
                        if (dummyResolver != null)
                                db.setEntityResolver(dummyResolver);
                        mDoc = db.parse(is);
                       
                       
                       
                } catch (java.io.IOException ioe) {
                        this.logger.severe("Error loading xml file:" + ioe.getMessage());
                        throw ioe;

                } catch (org.xml.sax.SAXException sax) {
                        this.logger.severe("Error parsing xml file:" + sax.getMessage());
                        throw sax;

                } catch (ParserConfigurationException pce) {
                        this.logger.severe("Error in sax parser configuration :"
                                        + pce.getMessage());
                        throw pce;
                }finally{
                        is.close();
                }

                this.logger.exiting("XmlDocument", "loadFromFile");
        }

        /**
         * Makes the parser to ignore the dtd.
         */
        public void ignoreDTD() {
                dummyResolver = new EntityResolver() {

                        @Override
                        public InputSource resolveEntity(String publicId, String systemId)
                                        throws SAXException, IOException {
                                return new InputSource(
                                                new ByteArrayInputStream(
                                                                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                                                                                .getBytes()));

                        }
                };

        }

        /**
         * makes this XmlDocument to be an empty xml document.
         *
         * @return the xml document
         *
         * @throws ParserConfigurationException the parser configuration exception
         */
        public XmlDocument emptyDocument() throws ParserConfigurationException {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                dbf.setValidating(mValidate);
                dbf.setNamespaceAware(mNamespace);
                DocumentBuilder db = dbf.newDocumentBuilder();
                db.setErrorHandler(mErrHandler);
                if (dummyResolver != null)
                        db.setEntityResolver(dummyResolver);
                if (this.mDocType != null)
                        mDoc = db.getDOMImplementation().createDocument(null, null,
                                        this.mDocType);
                else
                        mDoc = db.newDocument();
                logger.info("empty doc created");
                return this;

        }

        /**
         * Validates the Xml document against its dtd and returns true if the
         * validation went ok. This method may consume many resources so call only
         * if necessary
         *
         * @return true, if force validation
         */
        public boolean forceValidation() {
                /*
                 * The only way to know if a built xmlDocument is valid is to reparse it
                 * again, instead of doing it writting it to a file, this method does
                 * the process in a memory buffer.
                 */

                logger.entering(this.getClass().getName(), "forceValidation");
                if (!mValidate)
                        return false;

                // if not the value will be updated
                this.mValid = true;
                StringWriter xmlBuffer = new StringWriter();

                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                dbf.setValidating(mValidate);
                dbf.setNamespaceAware(mNamespace);
                try {
                        DocumentBuilder db = dbf.newDocumentBuilder();
                        db.setErrorHandler(mErrHandler);
                        if (dummyResolver != null)
                                db.setEntityResolver(dummyResolver);
                        // save the document into the StringWriter
                        this.save(xmlBuffer);
                        xmlBuffer.flush();
                        // input stream from StringBuffer
                        ByteArrayInputStream xmlParseInputStream = new ByteArrayInputStream(
                                        xmlBuffer.toString().getBytes("UTF-8"));
                        // and parse again
                        db.parse(xmlParseInputStream);
                } catch (Exception e) {

                        // e.printStackTrace();
                        logger.warning("Error validating file: " + e.getMessage());
                        return false;
                }
                return this.mValid;

        }

        /**
         * Validate against schema.
         *
         * @param schemaUrl the schema url
         * @param schemaLanguage the schema language
         * @param factoryClassName the factory class name
         *
         * @return the xml document
         */
        public XmlDocument validateAgainstSchema(URL schemaUrl, String schemaLanguage, String factoryClassName) {
                this.mValid = true;
                SchemaFactory factory = null;
                XmlDocument result = null;
                try {
                       
                        logger.info("SchemaLanguage: "+schemaLanguage);
                        if(factoryClassName!=null){
                                logger.info("SchemaFactory: "+factoryClassName+" "+this.getClass().getClassLoader().loadClass(factoryClassName));
                                factory = SchemaFactory.newInstance(schemaLanguage, factoryClassName, this.getClass().getClassLoader());
                        }else
                                factory = SchemaFactory.newInstance(schemaLanguage);
                        logger.info("Schema implementation:"+factory.getClass().getName());
                        factory.setErrorHandler(mErrHandler);

                        StreamSource ss;

                        ss = new StreamSource(schemaUrl.openStream());

                        ss.setSystemId(schemaUrl.toExternalForm());

                        Schema schema;
                        schema = factory.newSchema(ss);
                       
                        // and continue
                        javax.xml.validation.Validator validator = schema.newValidator();
                        StringWriter xmlBuffer = new StringWriter();
                        this.save(xmlBuffer);
                        xmlBuffer.flush();
                        // input stream from StringBuffer
                        ByteArrayInputStream xmlInputStream = new ByteArrayInputStream(
                                        xmlBuffer.toString().getBytes("UTF-8"));
                       
                       
                        Source src = new SAXSource(new InputSource(xmlInputStream));
                        src.setSystemId(schemaUrl.toExternalForm());
                       
                        result = new XmlDocument().emptyDocument();
                       
                        validator.validate( src,new DOMResult(result.getDocument()));

                } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println(e.getLocalizedMessage());
                       
                        logger.warning(e.getClass().getName() + ": Error validating file: "
                                        + e.getMessage());
                        return result;
                }
                return result;

        }

        /**
         * Checks if is valid.
         *
         * @return true, if is valid
         */
        public boolean isValid() {
                return this.mValid;
        }

        /**
         * Sets the validating.
         *
         * @param bool the new validating
         */
        public void setValidating(boolean bool) {

                this.mValidate = bool;
        }

        /**
         * Excute an Xpath query against the document. The Namespace context is
         * mandatory if the xml is using xml namespaces see {@link NamespaceContext}
         * for more info.
         *
         * @param query the xpath query
         * @param nsc the nsc
         *
         * @return a NodeList with the result set
         *
         * @throws XPathException the xpath compilation exception
         * @throws IllegalArgumentException the illegal argument exception
         */
        public NodeList excuteXpathQuery(String query, NamespaceContext nsc)
                        throws XPathException, IllegalArgumentException {
                this.logger.entering("XmlDocument", "excuteXpathQuery");
                try {
                        if (mDoc == null)
                                throw new NullPointerException("There is no document loaded");
                        // get the query from the cache
                        XPathExpression expr = this.mCache.compileXpath(query, nsc);
                        logger.info("Executing Xpath: " + query);
                        Object result = expr.evaluate(mDoc, XPathConstants.NODESET);  

                        this.logger.exiting("XmlDocument", "excuteXpathQuery");

                        return (NodeList) result;
                } catch (XPathException xpa) {
                        this.logger.severe("Error forming the xpath expresion:"
                                        + xpa.getMessage());
                        throw xpa;
                } catch (IllegalArgumentException iae) {
                        this.logger.severe("Illegal argument during xpath evaluation:"
                                        + iae.getMessage());
                        throw iae;
                }

        }

        /**
         * Excute an Xpath query against the document. The Namespace context is
         * mandatory if the xml is using xml namespaces see {@link NamespaceContext}
         * for more info.
         *
         * @param query the xpath query
         * @param nsc the nsc
         * @param returnType the return type
         *
         * @return a NodeList with the result set
         *
         * @throws XPathException the xpath compilation exception
         * @throws IllegalArgumentException the illegal argument exception
         */
        public Object excuteXpathQuery(String query, NamespaceContext nsc,QName returnType)
                        throws XPathException, IllegalArgumentException {
                this.logger.entering("XmlDocument", "excuteXpathQuery");
                try {
                        if (mDoc == null)
                                throw new NullPointerException("There is no document loaded");
                        // get the query from the cache
                        XPathExpression expr = this.mCache.compileXpath(query, nsc);
                        logger.info("Executing Xpath: " + query);
                        Object result = expr.evaluate(mDoc, returnType);  

                        this.logger.exiting("XmlDocument", "excuteXpathQuery");

                        return result;
                } catch (XPathException xpa) {
                        this.logger.severe("Error forming the xpath expresion:"
                                        + xpa.getMessage());
                        throw xpa;
                } catch (IllegalArgumentException iae) {
                        this.logger.severe("Illegal argument during xpath evaluation:"
                                        + iae.getMessage());
                        throw iae;
                }

        }

       
       
        /**
         * Excute a compiled xpath query against the document.
         *
         * @param query the query
         *
         * @return the node list
         *
         * @throws XPathException the x path exception
         * @throws IllegalArgumentException the illegal argument exception
         */
        public NodeList excuteCompiledXpathQuery(XPathExpression query)
                        throws XPathException, IllegalArgumentException {
                this.logger.entering("XmlDocument", "excuteCompiledXpathQuery");
                try {
                        if (mDoc == null)
                                throw new NullPointerException("There is no document loaded");
                        Object result = query.evaluate(mDoc, XPathConstants.NODESET);
                        this.logger.exiting("XmlDocument", "excuteCompiledXpathQuery");
                        return (NodeList) result;
                } catch (XPathException xpa) {
                        this.logger.severe("Error forming the xpath expresion:"
                                        + xpa.getMessage());
                        throw xpa;
                } catch (IllegalArgumentException iae) {
                        this.logger.severe("Illegal argument during xpath evaluation:"
                                        + iae.getMessage());
                        throw iae;
                }

        }

        /**
         * Sets the namespace aware, in case the xml is using name spaces.
         *
         * @param bool the new namespace aware
         */
        public void setNamespaceAware(boolean bool) {

                this.mNamespace = bool;
        }

        /**
         * Gets the java xml document.
         *
         * @return the document
         */
        public Document getDocument() {
                return this.mDoc;

        }

        /**
         * Sets the java xml document.
         *
         * @param xDoc the new document
         */
        public void setDocument(Document xDoc) {
                this.mDoc = xDoc;

        }

        /**
         * Save the xml to a file.
         *
         * @param file the file in which the xml will be stored
         *
         * @throws TransformerException the transformer exception
         * @throws IOException Signals that an I/O exception has occurred.
         */
        public void save(File file) throws TransformerException, IOException {
                try {
                        logger.info("Saving xml document to file :"
                                        + file.getAbsolutePath());
                        OutputStreamWriter out = new OutputStreamWriter(
                                        new FileOutputStream(file), "UTF-8");
                        this.save(out);

                } catch (IOException ioe) {
                        logger.log(Level.SEVERE, null, ioe);
                        throw ioe;
                }
        }

        /**
         * Saves the xml document using the Writer element.
         *
         * @param w the writer
         *
         * @throws TransformerException the transformer exception
         * @throws IOException Signals that an I/O exception has occurred.
         */
        public void save(Writer w) throws TransformerException, IOException {
                /*
                 * The way of saving the xml document is applying to it an empty xslt
                 * transformation.
                 */
                try {

                        Transformer transformer = TransformerFactory.newInstance()
                                        .newTransformer();

                        transformer.setOutputProperty(OutputKeys.INDENT, "no");
                        // transformer.setOutputProperty(
                        // "{http://xml.apache.org/xslt}indent-amount", "4");
                        // if there is a doc type set use the output props
                        // to store it
                        if (mDocType != null) {
                                if (mDocType.getPublicId() != null)
                                        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC,
                                                        mDocType.getPublicId());
                                if (mDocType.getSystemId() != null)
                                        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,
                                                        mDocType.getSystemId());

                        }
                        StreamResult result = new StreamResult(w);
                        DOMSource source = new DOMSource(this.mDoc);

                        transformer.transform(source, result);
                        logger.info("xml document saved into a writer");
                } catch (TransformerConfigurationException ex) {
                        logger.log(Level.SEVERE, null, ex);
                        throw ex;

                } catch (TransformerException ex) {
                        logger.log(Level.SEVERE, null, ex);
                        throw ex;

                }finally{
                        w.close();
                }
        }

        /**
         * Pretty prints the xml to the std out.
         */
        /*
        public void prettyPrint() {
                try {
                        OutputFormat format = new OutputFormat(this.getDocument());
                        format.setLineWidth(65);
                        format.setIndenting(true);
                        format.setIndent(2);
                        XMLSerializer serializer = new XMLSerializer(System.out, format);
                        serializer.serialize(this.getDocument());
                } catch (Exception e) {
                        logger.warning("error printing the xml file");
                }

        }
*/
        /**
         * To pretty string.
         *
         * @return the string
         */
//        public String toPrettyString() {
//                try {
//                        StringWriter sw = new StringWriter();
//                        OutputFormat format = new OutputFormat(this.getDocument());
//                        format.setLineWidth(65);
//                        format.setIndenting(true);
//                        format.setIndent(2);
//                        XMLSerializer serializer = new XMLSerializer(sw, format);
//                        serializer.serialize(this.getDocument());
//                        return sw.toString();
//                } catch (Exception e) {
//                        logger.warning("error printing the xml file");
//                        return null;
//                }
//
//        }

        /**
         * Sets the document type.
         *
         * @param dt the new document type
         */
        public void setDocumentType(DocumentType dt) {
                logger.entering(this.getClass().getName(), "setDocument");
                this.mDocType = dt;
        }

        /**
         * Gets the err handler.
         *
         * @return the err handler
         */
        public ErrorHandler getErrHandler() {
                return mErrHandler;
        }

        /**
         * Sets the err handler.
         *
         * @param errHandler the new err handler
         */
        public void setErrHandler(ErrorHandler errHandler) {
                this.mErrHandler = errHandler;
        }
}

class XpathCache {
        /**
         * this class indexes the XPath expresion which have been already compiled
         * {"xpathString"->{NamespaceContext->compiled expresion}
         */
        private HashMap<String, HashMap<NamespaceContext, XPathExpression>> firstLevel = new HashMap<String, HashMap<NamespaceContext, XPathExpression>>();
        private Logger logger = Logger.getLogger("nl.dedicon.converter.core.xml");

        public XpathCache() {

        }

        /**
         * If the xpath query has been already compiled will return it otherwise
         * will be compiled and stored.
         *
         * @param xPath
         * @param nsContext
         * @return the compiled xpath
         * @throws XPathExpressionException
         */
        public XPathExpression compileXpath(String xPath, NamespaceContext nsContext)
                        throws XPathExpressionException {

                logger.fine("Xpath caching management: " + xPath);
                logger.warning(this.descSize());
               
               
                // if the there is no xpath entry its created and then compiled and
                // stored
                if (!firstLevel.containsKey(xPath)) {
                        this.createXPathEntry(xPath);
                        return this.compileAndStore(xPath, nsContext);
                } else {
                        // the xpath exists but with a different name space, the entry is
                        // created and compiled
                        if (!firstLevel.get(xPath).containsKey(nsContext)) {
                                return this.compileAndStore(xPath, nsContext);
                        } else {
                                // we have it! just return
                                return (XPathExpression) firstLevel.get(xPath).get(nsContext);
                        }

                }
        }

        /**
         * Compiles the xpath query and stores it into the cache
         *
         * @param xPath
         * @param nsContext
         * @return the compiled expresion
         * @throws XPathExpressionException
         */
        private XPathExpression compileAndStore(String xPath,
                        NamespaceContext nsContext) throws XPathExpressionException {

                XPathFactory factory = XPathFactory.newInstance();
                XPath xpath = factory.newXPath();
                if (nsContext != null)
                        xpath.setNamespaceContext(nsContext);

                XPathExpression expr = xpath.compile(xPath);
                logger.fine("xpath compiled ");

                this.firstLevel.get(xPath).put(nsContext, expr);
                logger.info("xpath stored " + xPath);
                return expr;
        }

        /**
         * Creates the cache entry for this xpath expression
         *
         */
        private void createXPathEntry(String xPath) {
                logger.fine("Creating xpath cache entry: " + xPath);
                this.firstLevel.put(xPath,
                                new HashMap<NamespaceContext, XPathExpression>());
        }
       
        private String descSize(){
                String size="XML CACHE SIZE:\n";
                size+="\tFirst level:"+firstLevel.size()+"\n";
                int secondSizes=0;
                for(String key: this.firstLevel.keySet()){
                        secondSizes+=this.firstLevel.get(key).size();
                }
                size+="\tSecond levels:"+secondSizes;
                return size;
        }
}


