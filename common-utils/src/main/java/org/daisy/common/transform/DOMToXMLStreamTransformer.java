package org.daisy.common.transform;

import java.util.function.Supplier;
import java.util.Iterator;

import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Document;

/*
 * Note that this interface does not extend javax.xml.transform.Transformer.
 */
public interface DOMToXMLStreamTransformer {
	
	/**
	 * Transform a sequence of input documents to a sequence of output documents.
	 *
	 * @param input A sequence of DOM objects. Allowed to throw TransformerException.
	 * @param output A supplier of XMLStreamWriters. Allowed to throw TransformerException.
	 * @throws TransformerException
	 */
	public void transform(Iterator<Document> input, Supplier<XMLStreamWriter> output) throws TransformerException;
	
}
