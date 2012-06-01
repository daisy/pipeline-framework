package org.daisy.pipeline.webservice;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.thaiopensource.resolver.Identifier;
import com.thaiopensource.resolver.Input;
import com.thaiopensource.resolver.Resolver;
import com.thaiopensource.resolver.ResolverException;
import com.thaiopensource.util.PropertyMapBuilder;
import com.thaiopensource.validate.SchemaReader;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.rng.CompactSchemaReader;
import com.thaiopensource.xml.sax.DraconianErrorHandler;

public class XmlValidator {

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(Validator.class.getName());

	public boolean validate(Document document, URL schemaUrl) {
		HandlerImpl handler = new HandlerImpl();

		// validator is not thread-safe
		// http://www.thaiopensource.com/relaxng/api/jing/com/thaiopensource/validate/Validator.html
		com.thaiopensource.validate.Validator validator = createValidator(schemaUrl, handler);

		ContentHandler contentHandler = validator.getContentHandler();
		handler.setContentHandler(contentHandler);

		try {
			InputSource documentInput = DOMtoInputSource(document);
			SAXParser parser = createParser();
			parser.parse(documentInput, handler);
		} catch (ParserConfigurationException e) {
			logger.error(e.getMessage());
			return false;
		} catch (SAXException e) {
			logger.error(e.getMessage());
			return false;
		} catch (IOException e) {
			logger.error(e.getMessage());
			return false;
		} catch (TransformerConfigurationException e) {
			logger.error(e.getMessage());
			return false;
		} catch (TransformerException e) {
			logger.error(e.getMessage());
			return false;
		} catch (TransformerFactoryConfigurationError e) {
			logger.error(e.getMessage());
			return false;
		}

		return !handler.hasErrors();
	}

	// create an input source from a DOM document
	private InputSource DOMtoInputSource(Document doc) throws TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError {
		DOMSource source = new DOMSource(doc);
		StringWriter xmlAsWriter = new StringWriter();
		StreamResult result = new StreamResult(xmlAsWriter);
		TransformerFactory.newInstance().newTransformer().transform(source, result);
		StringReader xmlReader = new StringReader(xmlAsWriter.toString());
		InputSource is = new InputSource(xmlReader);
		return is;
	}
	private SAXParser createParser() throws ParserConfigurationException, SAXException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		SAXParser parser = factory.newSAXParser();
		return parser;
	}

	private com.thaiopensource.validate.Validator createValidator(URL schemaUrl, HandlerImpl handler) {
		try {
			// make the Schema object
			InputSource schemaSource = new InputSource(schemaUrl.toString());
			PropertyMapBuilder schemaMapBuilder = new PropertyMapBuilder();
			schemaMapBuilder.put(ValidateProperty.RESOLVER, BasicResolver.getInstance());
			schemaMapBuilder.put(ValidateProperty.ERROR_HANDLER, new DraconianErrorHandler());
			SchemaReader schemaReader = CompactSchemaReader.getInstance();
			com.thaiopensource.validate.Schema schema = schemaReader.createSchema(schemaSource, schemaMapBuilder.toPropertyMap());

			// make the Validator
			PropertyMapBuilder validatorMapBuilder = new PropertyMapBuilder();
			// send validation errors to our ErrorHandler overrides
			validatorMapBuilder.put(ValidateProperty.ERROR_HANDLER, handler);
			com.thaiopensource.validate.Validator validator = schema.createValidator(validatorMapBuilder.toPropertyMap());
			return validator;
		} catch (RuntimeException e) {
			logger.error(e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	/**
	 * Basic Resolver from Jing modified to add support for resolving zip and
	 * jar relative locations.
	 *
	 * @author george@oxygenxml.com
	 */
	static public class BasicResolver implements Resolver {
		static private final BasicResolver theInstance = new BasicResolver();

		protected BasicResolver() {
		}

		public static BasicResolver getInstance() {
			return theInstance;
		}

		@Override
		public void resolve(Identifier id, Input input) throws IOException,
				ResolverException {
			if (!input.isResolved()) {
				input.setUri(resolveUri(id));
			}
		}

		@Override
		public void open(Input input) throws IOException, ResolverException {
			if (!input.isUriDefinitive()) {
				return;
			}
			URI uri;
			try {
				uri = new URI(input.getUri());
			} catch (URISyntaxException e) {
				throw new ResolverException(e);
			}
			if (!uri.isAbsolute()) {
				throw new ResolverException("cannot open relative URI: " + uri);
			}
			URL url = new URL(uri.toASCIIString());
			// XXX should set the encoding properly
			// XXX if this is HTTP and we've been redirected, should do
			// input.setURI with the new URI
			input.setByteStream(url.openStream());
		}

		public static String resolveUri(Identifier id) throws ResolverException {
			try {
				String uriRef = id.getUriReference();
				URI uri = new URI(uriRef);
				if (!uri.isAbsolute()) {
					String base = id.getBase();
					if (base != null) {
						// OXYGEN PATCH START
						// Use class URL in order to resolve protocols like zip
						// and jar.
						URI baseURI = new URI(base);
						if ("zip".equals(baseURI.getScheme())
								|| "jar".equals(baseURI.getScheme())) {
							uriRef = new URL(new URL(base), uriRef)
									.toExternalForm();
							// OXYGEN PATCH END
						} else {
							uriRef = baseURI.resolve(uri).toString();
						}
					}
				}

				return uriRef;
			} catch (URISyntaxException e) {
				throw new ResolverException(e);
			} catch (MalformedURLException e) {
				throw new ResolverException(e);
			}
		}
	}

	// the annoying thing is that Jing's validator's contentHandler isn't enough for SAX
	// SAX wants a DefaultHandler.  So we made one for passing off events...
	public class HandlerImpl extends DefaultHandler implements ErrorHandler {
		ContentHandler contentHandler;
		boolean hasErrors;

		HandlerImpl() {
			hasErrors = false;
		}
		public boolean hasErrors() {
			return hasErrors;
		}

		// give it a content handler to pass off the events to
		public void setContentHandler(ContentHandler handler) {
			contentHandler = handler;
		}

		// error callbacks
		@Override
		public void error(SAXParseException exception) throws SAXException {
			exception.printStackTrace();
			hasErrors = true;
		}

		@Override
		public void fatalError(SAXParseException exception) throws SAXException {
			exception.printStackTrace();
			hasErrors = true;
		}

		@Override
		public void warning(SAXParseException exception) throws SAXException {
			exception.printStackTrace();
			hasErrors = true;
		}

		// content callbacks
		@Override
		public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
			contentHandler.characters(arg0, arg1, arg2);
		}

		@Override
		public void endDocument() throws SAXException {
			contentHandler.endDocument();
		}

		@Override
		public void endElement(String arg0, String arg1, String arg2) throws SAXException {
			contentHandler.endElement(arg0, arg1, arg2);
		}

		@Override
		public void endPrefixMapping(String arg0) throws SAXException {
			contentHandler.endPrefixMapping(arg0);
		}

		@Override
		public void ignorableWhitespace(char[] arg0, int arg1, int arg2) throws SAXException {
			contentHandler.ignorableWhitespace(arg0, arg1, arg2);
		}

		@Override
		public void processingInstruction(String arg0, String arg1) throws SAXException {
			contentHandler.processingInstruction(arg0, arg1);
		}

		@Override
		public void setDocumentLocator(Locator locator) {
			contentHandler.setDocumentLocator(locator);
		}

		@Override
		public void skippedEntity(String arg0) throws SAXException {
			contentHandler.skippedEntity(arg0);
		}

		@Override
		public void startDocument() throws SAXException {
			contentHandler.startDocument();
		}

		@Override
		public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
			contentHandler.startElement(namespaceURI, localName, qName, atts);
		}

		@Override
		public void startPrefixMapping(String arg0, String arg1) throws SAXException {
			contentHandler.startPrefixMapping(arg0, arg1);
		}
	}
}
