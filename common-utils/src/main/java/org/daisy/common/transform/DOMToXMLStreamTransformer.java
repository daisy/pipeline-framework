package org.daisy.common.transform;

import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;

/*
 * Note that this interface does not extend javax.xml.transform.Transformer.
 */
public interface DOMToXMLStreamTransformer {
	
	public void transform(Document document, XMLStreamWriter writer) throws TransformerException;
	
}
