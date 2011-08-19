package org.daisy.converter.parser.stax;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class StaxConverterParser parses the converter information present in a xpl file.
 * This information must follow the following syntax
 * 
 * <cd:converter name="testHello" version="1.0">
		<cd:description> Test xpl description</cd:description>	
		<cd:arg  name="in"  type="input" port="source" desc="input for hello process" optional="true"/> 	
		<cd:arg  name="out"  type="output" port="result" desc="the result file"/> 	
		<cd:arg  name="o"  type="option" bind="opt" desc="that kind of option that modifies the converter behaviour"/>
		<cd:arg  name="msg"  type="parameter" bind="msg" port="params" desc="msg to show" />
    </cd:converter>	
 * 
 * 
 * 
 * 
 * 
 */
public class StaxConverterParser implements ConverterParser {

	/** The xmlinputfactory. */
	private XMLInputFactory mFactory;
	
	/** The uri resolver. */
	private URIResolver mUriResolver;

	private static Logger mLogger = LoggerFactory.getLogger(StaxConverterParser.class);
	/* (non-Javadoc)
	 * @see org.daisy.converter.parser.ConverterParser#parse(org.daisy.pipeline.modules.converter.ConverterDescriptor, org.daisy.converter.parser.ConverterBuilder)
	 */
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
			try {
				builder.withURI(descUrl.toURI());
			} catch (URISyntaxException e) {
				mLogger.warn("resolved uri not an uri");
			}
			is = descUrl.openConnection().getInputStream();
			reader = mFactory.createXMLEventReader(is);

			//converter elem and attrs
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
			//arguments
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

	/**
	 * Sets the factory.
	 *
	 * @param inputFactoryProvider the new factory
	 */
	public void setFactory(Stax2InputFactoryProvider inputFactoryProvider) {
		//System.out.println("FACTORY!");
		mFactory = inputFactoryProvider.createInputFactory();
	}
	
	/**
	 * Sets the direct factory.
	 *
	 * @param fact the new direct factory
	 */
	public void setDirectFactory(XMLInputFactory fact) {
		mFactory = fact.newInstance();
	}

	/**
	 * Sets the uri resover.
	 *
	 * @param resolver the new uri resover
	 */
	public void setUriResover(UriResolverDecorator resolver){
		//System.out.println("RESOLVER!");
		this.mUriResolver=resolver;
	}
	
	/**
	 * Parses the arguments.
	 *
	 * @param reader the reader
	 * @param builder the builder
	 * @throws XMLStreamException the xML stream exception
	 */
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
						cab.withBindType(argument.getAttributeByName(Attributes.BIND_TYPE).getValue());
						cab.withBind(argument.getAttributeByName(Attributes.BIND).getValue());
						cab.withDir(argument.getAttributeByName(Attributes.DIR).getValue());
						cab.withMediaType(argument.getAttributeByName(Attributes.MEDIA_TYPE).getValue());
						
						Attribute optional=argument.getAttributeByName(Attributes.OPTIONAL);
						Attribute sequence = argument.getAttributeByName(Attributes.SEQUENCE);
						Attribute type = argument.getAttributeByName(Attributes.TYPE);
						
						
						if (optional!=null){
							cab.withOptional(optional.getValue());
						}
						if (sequence!=null){
							cab.withSequence(sequence.getValue());
						}
						if (type!=null){
							cab.withType(type.getValue());
						}
						builder.withArgument(cab);
					}
				});
	}
	
	

	
}
