package org.daisy.common.saxon;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.TransformerException;

import net.sf.saxon.Configuration;
import net.sf.saxon.event.PipelineConfiguration;
import net.sf.saxon.event.Receiver;
import net.sf.saxon.event.StreamWriterToReceiver;
import net.sf.saxon.evpull.Decomposer;
import net.sf.saxon.evpull.EventIteratorOverSequence;
import net.sf.saxon.evpull.EventToStaxBridge;
import net.sf.saxon.om.NamespaceResolver;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.XdmDestination;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.sxpath.XPathDynamicContext;
import net.sf.saxon.sxpath.XPathEvaluator;
import net.sf.saxon.sxpath.XPathExpression;
import net.sf.saxon.trans.XPathException;

import org.daisy.common.stax.XMLStreamWriterHelper.BufferedXMLStreamWriter;
import org.daisy.common.stax.XMLStreamWriterHelper.FutureWriterEvent;
import org.daisy.common.stax.XMLStreamWriterHelper.WriterEvent;
import org.daisy.common.transform.XMLStreamToXMLStreamTransformer;

public final class SaxonHelper {
	
	public static javax.xml.namespace.QName jaxpQName(QName name) {
		String prefix = name.getPrefix();
		String ns = name.getNamespaceURI();
		String localPart = name.getLocalName();
		if (prefix != null)
			return new javax.xml.namespace.QName(ns, localPart, prefix);
		else
			return new javax.xml.namespace.QName(ns, localPart);
	}
	
	public static XPathExpression compileExpression(String expression, Hashtable<String,String> namespaceBindings, Configuration configuration)
			throws XPathException {
		XPathEvaluator xpathEvaluator = new XPathEvaluator(configuration);
		xpathEvaluator.getStaticContext().setNamespaceResolver(new MatchingNamespaceResolver(namespaceBindings));
		return xpathEvaluator.createPattern(expression);
	}
	
	public static boolean evaluateBoolean(XPathExpression expression, XdmNode contextNode) {
		try {
			XPathDynamicContext context = expression.createDynamicContext(contextNode.getUnderlyingNode());
			return expression.effectiveBooleanValue(context);
		} catch (XPathException e) {
			return false;
		}
	}
	
	public static XdmNode transform(NodeInfo element, XMLStreamToXMLStreamTransformer transformer, Configuration configuration)
			throws TransformerException {
		try {
			PipelineConfiguration pipeConfig = new PipelineConfiguration(configuration);
			XMLStreamReader reader
				= new EventToStaxBridge(
					new Decomposer(
						new EventIteratorOverSequence(element.iterate()), pipeConfig), pipeConfig);
			XdmDestination destination = new XdmDestination();
			Receiver receiver = destination.getReceiver(configuration);
			receiver.open();
			WriterImpl writer = new WriterImpl(receiver);
			transformer.transform(reader, writer);
			receiver.close();
			return destination.getXdmNode();
		} catch (Exception e) {
			throw new TransformerException(e);
		}
	}
	
	// copied from com.xmlcalabash.util.ProcessMatch
	public static class MatchingNamespaceResolver implements NamespaceResolver {
		
		private Hashtable<String,String> ns = new Hashtable<String,String>();
		
		public MatchingNamespaceResolver(Hashtable<String,String> bindings) {
			ns = bindings;
		}
		
		public String getURIForPrefix(String prefix, boolean useDefault) {
			if ("".equals(prefix) && !useDefault) {
				return "";
			}
			return ns.get(prefix);
		}
		
		public Iterator<String> iteratePrefixes() {
			Vector<String> p = new Vector<String> ();
			for (String pfx : ns.keySet()) {
				p.add(pfx);
			}
			return p.iterator();
		}
	}
	
	private static class WriterImpl extends StreamWriterToReceiver implements BufferedXMLStreamWriter {
		
		WriterImpl(Receiver receiver) {
			super(receiver);
		}
		
		private Queue<WriterEvent> queue = new LinkedList<>();
		
		public void writeEvent(FutureWriterEvent event) throws XMLStreamException {
			queue.add(event);
			flushQueue();
		}
		
		private boolean isQueueEmpty() {
			return queue == null || queue.isEmpty();
		}
		
		private boolean flushQueue() throws XMLStreamException {
			if (queue == null)
				return true;
			List<WriterEvent> todo = null;
			while (!queue.isEmpty()) {
				WriterEvent event = queue.peek();
				if (event instanceof FutureWriterEvent && !((FutureWriterEvent)event).isReady())
					break;
				if (todo == null)
					todo = new ArrayList<WriterEvent>();
				todo.add(event);
				queue.remove(); }
			Queue<WriterEvent> tmp = queue;
			queue = null;
			if (todo != null)
				for (WriterEvent event : todo)
					event.writeTo(this);
			queue = tmp;
			return queue.isEmpty();
		}

		@Override
		public void flush() throws XMLStreamException {
			if (!flushQueue())
				throw new XMLStreamException("not ready");
			super.flush();
		}

		@Override
		public void close() throws XMLStreamException {
			throw new UnsupportedOperationException();
		}

		@Override
		public NamespaceContext getNamespaceContext() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getPrefix(String uri) {
			if (!isQueueEmpty())
				throw new IllegalStateException();
			return super.getPrefix(uri);
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
		public void setPrefix(String prefix, String uri) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void writeAttribute(String localName, String value) throws XMLStreamException {
			if (flushQueue())
				super.writeAttribute(localName, value);
			else
				queue.add(w -> w.writeAttribute(localName, value));
		}

		@Override
		public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
			if (flushQueue())
				super.writeAttribute(prefix, namespaceURI, localName, value);
			else
				queue.add(w -> w.writeAttribute(prefix, namespaceURI, localName, value));
		}

		@Override
		public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
			if (flushQueue())
				super.writeAttribute(namespaceURI, localName, value);
			else
				queue.add(w -> w.writeAttribute(namespaceURI, localName, value));
		}
		
		@Override
		public void writeCData(String text) throws XMLStreamException {
			if (flushQueue())
				super.writeCData(text);
			else
				queue.add(w -> w.writeCData(text));
		}

		@Override
		public void writeCharacters(String text) throws XMLStreamException {
			if (flushQueue())
				super.writeCharacters(text);
			else
				queue.add(w -> w.writeCharacters(text));
		}

		@Override
		public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void writeComment(String text) throws XMLStreamException {
			if (flushQueue())
				super.writeComment(text);
			else
				queue.add(w -> w.writeComment(text));
		}

		@Override
		public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
			if (flushQueue())
				super.writeDefaultNamespace(namespaceURI);
			else
				queue.add(w -> w.writeDefaultNamespace(namespaceURI));
		}

		@Override
		public void writeDTD(String dtd) throws XMLStreamException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void writeEmptyElement(String localName) throws XMLStreamException {
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
		public void writeEndElement() throws XMLStreamException {
			if (flushQueue())
				super.writeEndElement();
			else
				queue.add(w -> w.writeEndElement());
		}
		
		@Override
		public void writeEndDocument() throws XMLStreamException {
			if (flushQueue())
				super.writeEndDocument();
			else
				queue.add(w -> w.writeEndDocument());
		}

		@Override
		public void writeEntityRef(String name) throws XMLStreamException {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
			if (flushQueue())
				super.writeNamespace(prefix, namespaceURI);
			else
				queue.add(w -> w.writeNamespace(prefix, namespaceURI));
		}
		
		@Override
		public void writeProcessingInstruction(String target) throws XMLStreamException {
			if (flushQueue())
				super.writeProcessingInstruction(target);
			else
				queue.add(w -> w.writeProcessingInstruction(target));
		}

		@Override
		public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
			if (flushQueue())
				super.writeProcessingInstruction(target, data);
			else
				queue.add(w -> w.writeProcessingInstruction(target, data));
		}
		
		@Override
		public void writeStartDocument() throws XMLStreamException {
			if (flushQueue())
				super.writeStartDocument();
			else
				queue.add(w -> w.writeStartDocument());
		}

		@Override
		public void writeStartDocument(String version) throws XMLStreamException {
			if (flushQueue())
				super.writeStartDocument(version);
			else
				queue.add(w -> w.writeStartDocument(version));
		}

		@Override
		public void writeStartDocument(String encoding, String version) throws XMLStreamException {
			if (flushQueue())
				super.writeStartDocument(encoding, version);
			else
				queue.add(w -> w.writeStartDocument(encoding, version));
		}
		
		@Override
		public void writeStartElement(String localName) throws XMLStreamException {
			if (flushQueue())
				super.writeStartElement(localName);
			else
				queue.add(w -> w.writeStartElement(localName));
		}

		@Override
		public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
			if (flushQueue())
				super.writeStartElement(namespaceURI, localName);
			else
				queue.add(w -> w.writeStartElement(namespaceURI, localName));
		}

		@Override
		public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
			if (flushQueue())
				super.writeStartElement(prefix, localName, namespaceURI);
			else
				queue.add(w -> w.writeStartElement(prefix, localName, namespaceURI));
		}
	}
	
	private SaxonHelper() {
		// no instantiation
	}
}
