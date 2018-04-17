package org.daisy.common.calabash;

import java.net.URI;
import java.util.Stack;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.TransformerException;

import com.xmlcalabash.core.XProcRuntime;
import com.xmlcalabash.util.TreeWriter;

import net.sf.saxon.dom.DocumentOverNodeInfo;
import net.sf.saxon.s9api.XdmNode;

import org.daisy.common.transform.DOMToXMLStreamTransformer;

import org.w3c.dom.Document;

public final class XMLCalabashHelper {
	
	public static XdmNode transform(XdmNode document, DOMToXMLStreamTransformer transformer, XProcRuntime runtime)
			throws TransformerException {
		return transform(document, transformer, document.getBaseURI(), runtime);
	}
	
	public static XdmNode transform(XdmNode document, DOMToXMLStreamTransformer transformer, URI destBaseURI, XProcRuntime runtime)
			throws TransformerException {
		Document doc = (Document)DocumentOverNodeInfo.wrap(document.getUnderlyingNode());
		XMLStreamWriterOverTreeWriter writer = new XMLStreamWriterOverTreeWriter(runtime, destBaseURI);
		transformer.transform(doc, writer);
		return writer.getResult();
	}
	
	private static class XMLStreamWriterOverTreeWriter extends TreeWriter implements XMLStreamWriter {
		
		final URI baseURI;
		
		XMLStreamWriterOverTreeWriter(XProcRuntime runtime, URI baseURI) {
			super(runtime);
			this.baseURI = baseURI;
		}

		Stack<Boolean> contentStarted = new Stack<>();
		void startContentIfNotStartedYet() {
			if (!contentStarted.isEmpty() && !contentStarted.peek()) {
				startContent();
				contentStarted.pop();
				contentStarted.push(true);
			}
		}

		@Override
		public void close() throws XMLStreamException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void flush() throws XMLStreamException {
		}

		@Override
		public NamespaceContext getNamespaceContext() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getPrefix(String uri) throws XMLStreamException {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object getProperty(String name) throws IllegalArgumentException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setDefaultNamespace(String uri) throws XMLStreamException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setPrefix(String prefix, String uri) throws XMLStreamException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void writeAttribute(String localName, String value) throws XMLStreamException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
			addAttribute(new net.sf.saxon.s9api.QName(namespaceURI, localName), value);
		}

		@Override
		public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
			addAttribute(new net.sf.saxon.s9api.QName(prefix, namespaceURI, localName), value);
		}

		@Override
		public void writeCData(String data) throws XMLStreamException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void writeCharacters(String text) throws XMLStreamException {
			startContentIfNotStartedYet();
			addText(text);
		}

		@Override
		public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void writeComment(String data) throws XMLStreamException {
			addComment(data);
		}

		@Override
		public void writeDTD(String dtd) throws XMLStreamException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void writeEmptyElement(String localName) throws XMLStreamException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void writeEndDocument() throws XMLStreamException {
			endDocument();
		}

		@Override
		public void writeEndElement() throws XMLStreamException {
			addEndElement();
			contentStarted.pop();
		}

		@Override
		public void writeEntityRef(String name) throws XMLStreamException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
			addNamespace(prefix, namespaceURI);
		}

		@Override
		public void writeProcessingInstruction(String target) throws XMLStreamException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
			addPI(target, data);
		}

		@Override
		public void writeStartDocument() throws XMLStreamException {
			startDocument(baseURI);
		}

		@Override
		public void writeStartDocument(String version) throws XMLStreamException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void writeStartDocument(String encoding, String version) throws XMLStreamException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void writeStartElement(String localName) throws XMLStreamException {
			startContentIfNotStartedYet();
			addStartElement(new net.sf.saxon.s9api.QName(localName));
			contentStarted.push(false);
		}

		@Override
		public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
			startContentIfNotStartedYet();
			addStartElement(new net.sf.saxon.s9api.QName(namespaceURI, localName));
			contentStarted.push(false);
		}

		@Override
		public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
			startContentIfNotStartedYet();
			addStartElement(new net.sf.saxon.s9api.QName(prefix, namespaceURI, localName));
			contentStarted.push(false);
		}
	}
	
	private XMLCalabashHelper() {
		// no instantiation
	}
}
