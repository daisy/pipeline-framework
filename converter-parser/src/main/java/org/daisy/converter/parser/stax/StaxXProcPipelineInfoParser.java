package org.daisy.converter.parser.stax;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.LinkedList;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;

import org.codehaus.stax2.osgi.Stax2InputFactoryProvider;
import org.daisy.common.xproc.XProcOptionInfo;
import org.daisy.common.xproc.XProcPipelineInfo;
import org.daisy.common.xproc.XProcPipelineInfo.Builder;
import org.daisy.common.xproc.XProcPortInfo;
import org.daisy.converter.parser.XProcScriptConstants.Attributes;
import org.daisy.converter.parser.XProcScriptConstants.Elements;

public class StaxXProcPipelineInfoParser {
	/** The xmlinputfactory. */
	private XMLInputFactory mFactory;

	/** The uri resolver. */
	private URIResolver mUriResolver;

	

	
	public void setUriResolver(URIResolver uriResolver) {
		mUriResolver = uriResolver;
	}

	
	public void setFactory(XMLInputFactory factory) {
		this.mFactory = factory;
	}
	public XProcPipelineInfo parse(URI uri) {
		return new StatefulParser().parse(uri);
	}
	private class StatefulParser {
		private LinkedList<XMLEvent> mAncestors = new LinkedList<XMLEvent>();
		public XProcPipelineInfo parse(URI uri) {
			if (mFactory == null) {
				throw new IllegalStateException();
			}
			InputStream is = null;
			XMLEventReader reader = null;

			try {
				// init the XMLStreamReader
				// uRL descUrl = pipelineInfo.getURI().toURL();
				URL descUrl = uri.toURL();
				// if we have a url resolver resolve the
				if (mUriResolver != null) {
					Source src = mUriResolver.resolve(descUrl.toString(), "");
					descUrl = new URL(src.getSystemId());

				}
				is = descUrl.openConnection().getInputStream();
				reader = mFactory.createXMLEventReader(is);
				XProcPipelineInfo.Builder infoBuilder = new XProcPipelineInfo.Builder();
				infoBuilder.withURI(uri);
				parseInfoElements(reader, infoBuilder);
				return infoBuilder.build();
			} catch (XMLStreamException e) {
				throw new RuntimeException("Parsing error: " + e.getMessage(),
						e);
			} catch (IOException e) {
				throw new RuntimeException("io error while parsing"
						+ e.getMessage(), e);
			} catch (TransformerException te) {
				throw new RuntimeException("Error resolving url: "
						+ te.getMessage(), te);
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

		}

		public boolean isFirstChild() {
			return mAncestors.size() == 2;
		}

		private XMLEvent readNext(XMLEventReader reader)
				throws XMLStreamException {
			XMLEvent event = reader.nextEvent();

			if (event.isStartElement()) {
				mAncestors.add(event);
			} else if (event.isEndElement()) {
				mAncestors.pollLast();
			}
			return event;
		}

		private void parseInfoElements(final XMLEventReader reader,
				final Builder infoBuilder) throws XMLStreamException {

			while (reader.hasNext()) {
				XMLEvent event = readNext(reader);
				if (event.isStartElement()) {
					if (event.asStartElement().getName()
							.equals(Elements.P_OPTION)) {
						parseOption(event, infoBuilder);
					} else if (this.isFirstChild()
							&& event.asStartElement().getName()
									.equals(Elements.P_INPUT)
							|| event.asStartElement().getName()
									.equals(Elements.P_OUTPUT)) {
						parsePort(event, infoBuilder);
					}
				}
			}

		}

		private void parsePort(XMLEvent event, Builder infoBuilder) {
			QName elemName = event.asStartElement().getName();
			boolean primary = false;
			boolean sequence = false;
			String kind = "";
			Attribute portAttr = event.asStartElement().getAttributeByName(
					Attributes.PORT);
			Attribute kindAttr = event.asStartElement().getAttributeByName(
					Attributes.KIND);
			Attribute primaryAttr = event.asStartElement().getAttributeByName(
					Attributes.PRIMARY);
			Attribute sequenceAttr = event.asStartElement().getAttributeByName(
					Attributes.SEQUENCE);
			if (primaryAttr != null && primaryAttr.getValue().equals("true")) {
				primary = true;
			}
			if (sequenceAttr != null && sequenceAttr.getValue().equals("true")) {
				sequence = true;
			}
			XProcPortInfo info = null;
			if (portAttr != null) {
				if (kindAttr != null && elemName.equals(Elements.P_INPUT)
						&& kindAttr.getValue().equals("parameters")) {
					info = XProcPortInfo.newParameterPort(portAttr.getValue(),
							primary);
				} else if (elemName.equals(Elements.P_INPUT)) {
					info = XProcPortInfo.newInputPort(portAttr.getValue(),
							sequence, primary);
				} else if (elemName.equals(Elements.P_OUTPUT)) {
					info = XProcPortInfo.newOutputPort(portAttr.getValue(),
							sequence, primary);
				}
			}
			if (info != null) {
				infoBuilder.withPort(info);
			}

		}

		private void parseOption(final XMLEvent event, final Builder infoBuilder) {
			QName name = null;
			boolean required = false;
			String select = null;
			Attribute nameAttr = event.asStartElement().getAttributeByName(
					Attributes.NAME);
			Attribute requiredAttr = event.asStartElement().getAttributeByName(
					Attributes.REQUIRED);
			Attribute selectAttr = event.asStartElement().getAttributeByName(
					Attributes.SELECT);
			if (nameAttr != null) {
				name = new QName(nameAttr.getValue());
			}
			if (requiredAttr != null) {
				if (requiredAttr.getValue().equalsIgnoreCase("true")) {
					required = true;
				}
			}
			if (selectAttr != null) {
				select = selectAttr.getValue();
			}
			infoBuilder.withOption(XProcOptionInfo.newOption(name, required,
					select));

		}
	}
}
