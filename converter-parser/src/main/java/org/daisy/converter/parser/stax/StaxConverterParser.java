package org.daisy.converter.parser.stax;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;

import org.codehaus.stax2.osgi.Stax2InputFactoryProvider;
import org.daisy.common.stax.EventProcessor;
import org.daisy.common.stax.StaxEventHelper;
import org.daisy.common.stax.StaxEventHelper.EventPredicates;
import org.daisy.converter.parser.ConverterBuilder;
import org.daisy.converter.parser.ConverterBuilder.ConverterArgumentBuilder;
import org.daisy.converter.parser.ConverterDescriptorConstants.Attributes;
import org.daisy.converter.parser.ConverterDescriptorConstants.Elements;
import org.daisy.converter.parser.ConverterParser;
import org.daisy.pipeline.modules.UriResolverDecorator;
import org.daisy.pipeline.modules.converter.Converter;
import org.daisy.pipeline.modules.converter.ConverterDescriptor;

public class StaxConverterParser implements ConverterParser {

	private XMLInputFactory mFactory;
	private URIResolver mUriResolver;

	@Override
	public Converter parse(ConverterDescriptor descriptor, ConverterBuilder builder ) {
		if (mFactory == null) {
			throw new IllegalStateException();
		}
		InputStream is = null;
		XMLEventReader reader = null;
		try {
			// init the XMLStreamReader
			URL descUrl=descriptor.getFile().toURL();
			//if we have a url resolver resolve the 
			if (mUriResolver!=null){
				Source src =mUriResolver.resolve(descUrl.toString(), "");
				descUrl = new URL(src.getSystemId());
			}
			
			is = descUrl.openConnection().getInputStream();
			reader = mFactory.createXMLEventReader(is);

			// parse the package element
			StartElement elem = StaxEventHelper.peekNextElement(reader,
					Elements.CONVERTER);
			builder.withName(elem.getAttributeByName(Attributes.NAME)
					.getValue());
			builder.withVersion(elem.getAttributeByName(Attributes.VERSION)
					.getValue());
			elem = StaxEventHelper.peekNextElement(reader,
					Elements.DESC);
			reader.next();
			builder.withDescription(reader.getElementText());
			this.parseArguments(reader,builder);
			
			//builder.withDescription(elem.);

		} catch (XMLStreamException e) {
			throw new RuntimeException("Parsing error: " + e.getMessage(), e);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't access package descriptor: "
					+ e.getMessage(), e);
		}catch (TransformerException te){
			throw new RuntimeException("Error resolving url: "
					+ te.getMessage(), te);
		}finally {
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

	public void setFactory(Stax2InputFactoryProvider inputFactoryProvider) {
		//System.out.println("FACTORY!");
		mFactory = inputFactoryProvider.createInputFactory();
	}
	
	public void setDirectFactory(XMLInputFactory fact) {
		mFactory = fact.newInstance();
	}

	public void setUriResover(UriResolverDecorator resolver){
		//System.out.println("RESOLVER!");
		this.mUriResolver=resolver;
	}
	private void parseArguments(final XMLEventReader reader,
			final ConverterBuilder builder) throws XMLStreamException {
		StaxEventHelper.loop(reader, EventPredicates.IS_START_ELEMENT,
				EventPredicates.isElement(Elements.ARG),
				new EventProcessor() {
					public void process(XMLEvent event) {
						StartElement argument = event.asStartElement();
						ConverterArgumentBuilder cab = builder.getConverterArgumentBuilder();
						cab.withName(argument.getAttributeByName(Attributes.NAME).getValue());
						cab.withDesc(argument.getAttributeByName(Attributes.DESC).getValue());
						cab.withType(argument.getAttributeByName(Attributes.TYPE).getValue());
						Attribute bind=argument.getAttributeByName(Attributes.BIND);
						Attribute port=argument.getAttributeByName(Attributes.PORT);
						Attribute optional=argument.getAttributeByName(Attributes.OPTIONAL);
						if (bind!=null){
							cab.withBind(bind.getValue());
						}
						if (port!=null){
							cab.withPort(port.getValue());
						}
						if (optional!=null){
							cab.withOptional(optional.getValue());
						}
						builder.withArgument(cab);
					}
				});
	}
	
	

	
}
