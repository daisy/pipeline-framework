package org.daisy.expath.parser.stax;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.common.stax.EventProcessor;
import org.daisy.common.stax.StaxEventHelper;
import org.daisy.common.stax.StaxEventHelper.EventPredicates;
import org.daisy.expath.parser.EXPathConstants.Attributes;
import org.daisy.expath.parser.EXPathConstants.Elements;
import org.daisy.expath.parser.EXPathPackageParser;
import org.daisy.expath.parser.ModuleBuilder;
import org.daisy.pipeline.modules.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class StaxEXPathPackageParser implements EXPathPackageParser {

	private final static Logger logger = LoggerFactory
			.getLogger(StaxEXPathPackageParser.class);

	private static HashSet<QName> COMPONENT_ELEMENTS = new HashSet<QName>();
	static {
		COMPONENT_ELEMENTS.add(Elements.XSLT);
		COMPONENT_ELEMENTS.add(Elements.XPROC);
		COMPONENT_ELEMENTS.add(Elements.NG);
		COMPONENT_ELEMENTS.add(Elements.XSD);
		COMPONENT_ELEMENTS.add(Elements.XQUERY);
		COMPONENT_ELEMENTS.add(Elements.RNC);

	};

	private XMLInputFactory factory;

	public StaxEXPathPackageParser() {
	}
	
	public void activate(){
		logger.trace("Activating EXPath package parser");
	}

	public void setFactory(XMLInputFactory factory) {
		this.factory = factory;
	}

	public Module parse(URL url, ModuleBuilder builder) {
		logger.trace("parsing EXPath package <{}>", url);
		if (factory == null) {
			throw new IllegalStateException();
		}
		InputStream is = null;
		XMLEventReader reader = null;
		try {
			// init the XMLStreamReader
			is = url.openStream();
			reader = factory.createXMLEventReader(is);

			// parse the package element
			StartElement elem = StaxEventHelper.peekNextElement(reader,
					Elements.PACKAGE);
			// builder.withName(elem.getAttributeByName(Attributes.NAME)
			// .getValue());
			reader.next();

			// parse dependencies
			parseDependencies(reader, builder);

			// parse module
			parseModule(reader, builder);

		} catch (XMLStreamException e) {
			logger.trace("parsing error: {}", e.getMessage());
			throw new RuntimeException("Parsing error: " + e.getMessage(), e);

		} catch (IOException e) {
			logger.trace("parsing error: {}", e.getMessage());
			throw new RuntimeException("Couldn't access package descriptor: "
					+ e.getMessage(), e);
		} finally {
			try {
				if (reader != null)
					reader.close();
				if (is != null)
					is.close();
			} catch (Exception e) {
				// ignore;
			}
		}
		logger.trace("parsed <{}>", url);
		return builder.build();
	}

	private void parseDependencies(final XMLEventReader reader,
			final ModuleBuilder builder) throws XMLStreamException {
		StaxEventHelper.loop(reader, EventPredicates.IS_START_ELEMENT,
				EventPredicates.isElement(Elements.DEPENDENCY),
				new EventProcessor() {
					public void process(XMLEvent event) {
						StartElement dependency = event.asStartElement();
						builder.withDependency(
								dependency.getAttributeByName(Attributes.NAME)
										.getValue(),
								dependency.getAttributeByName(
										Attributes.VERSIONS).getValue());

					}
				});
	}

	private void parseModule(final XMLEventReader reader,
			final ModuleBuilder builder) throws XMLStreamException {
		StartElement module = StaxEventHelper.peekNextElement(reader,
				Elements.MODULE);

		// parse the version
		builder.withVersion(module.getAttributeByName(Attributes.VERSION)
				.getValue());
		// parse the name
		builder.withName(module.getAttributeByName(Attributes.NAME).getValue());
		// parse the title
		StaxEventHelper.peekNextElement(reader, Elements.TITLE);
		reader.next();
		builder.withTitle(reader.getElementText());

		// parse components
		parseComponents(reader, builder);

	}

	private void parseComponents(final XMLEventReader reader,
			final ModuleBuilder builder) throws XMLStreamException {
		// changed this because otherwise mvn won't work
		// this is really awkward

		Predicate<XMLEvent> pred = Predicates.or(
				EventPredicates.IS_START_ELEMENT,
				EventPredicates.IS_END_ELEMENT);
		StaxEventHelper.loop(reader, pred,
				EventPredicates.getChildOrSiblingPredicate(),
				new EventProcessor() {
					public void process(XMLEvent event)
							throws XMLStreamException {
						if (event.isStartElement()) {
							StartElement component = event.asStartElement();

							if (COMPONENT_ELEMENTS.contains(component.getName())) {
								StaxEventHelper.peekNextElement(reader,
										Elements.IMPORT_URI);
								reader.next();
								URI uri;
								try {
									uri = new URI(reader.getElementText());
								} catch (URISyntaxException e) {
									throw new XMLStreamException(
											e.getMessage(), e);
								}
								// Get the local path
								StaxEventHelper.peekNextElement(reader,
										Elements.FILE);
								reader.next();
								String path;
								path = reader.getElementText();

								builder.withComponent(uri, path);
							}
						}

					}
				});

	}

}
