package org.daisy.expath.parser.stax;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.codehaus.stax2.osgi.Stax2InputFactoryProvider;
import org.daisy.common.stax.EventProcessor;
import org.daisy.common.stax.StaxEventHelper;
import org.daisy.common.stax.StaxEventHelper.EventPredicates;
import org.daisy.expath.parser.EXPathConstants.Attributes;
import org.daisy.expath.parser.EXPathConstants.Elements;
import org.daisy.expath.parser.EXPathPackageParser;
import org.daisy.expath.parser.ModuleBuilder;
import org.daisy.pipeline.modules.Component.Space;
import org.daisy.pipeline.modules.Module;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class StaxEXPathPackageParser implements EXPathPackageParser {

	private XMLInputFactory factory;

	public StaxEXPathPackageParser() {
	}

	public void setFactory(Stax2InputFactoryProvider inputFactoryProvider) {
		factory = inputFactoryProvider.createInputFactory();
	}

	public Module parse(URL url, ModuleBuilder builder) {
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
			builder.withName(elem.getAttributeByName(Attributes.NAME)
					.getValue());
			reader.next();

			// parse dependencies
			parseDependencies(reader, builder);

			// parse module
			parseModule(reader, builder);

		} catch (XMLStreamException e) {
			throw new RuntimeException("Parsing error: " + e.getMessage(), e);
		} catch (IOException e) {
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

		// parse the title
		StaxEventHelper.peekNextElement(reader, Elements.TITLE);
		reader.next();
		builder.withTitle(reader.getElementText());

		// parse components
		parseComponents(reader, builder);

	}

	private void parseComponents(final XMLEventReader reader,
			final ModuleBuilder builder) throws XMLStreamException {
		//changed this because otherwise mvn won't work
		//this is really awkward
		
		Predicate<XMLEvent> pred = Predicates.or(
				EventPredicates.IS_START_ELEMENT,
				EventPredicates.IS_END_ELEMENT);
		StaxEventHelper.loop(reader, pred,
				EventPredicates.CHILD_OR_SIBLING, new EventProcessor() {
					public void process(XMLEvent event)
							throws XMLStreamException {
						if (event.isStartElement()) {
							StartElement component = event.asStartElement();
							// TODO plug other components xquery | xproc | xsd |
							// rng | rnc
							if (component.getName().equals(Elements.XSLT)) {
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

								builder.withComponent(uri, path, Space.XSLT);
							}
						}

					}
				});

	}

}
