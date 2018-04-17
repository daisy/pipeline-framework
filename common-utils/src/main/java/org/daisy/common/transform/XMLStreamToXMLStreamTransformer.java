package org.daisy.common.transform;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.TransformerException;

import org.daisy.common.stax.XMLStreamWriterHelper.BufferedXMLStreamWriter;

/*
 * Note that this interface does not extend javax.xml.transform.Transformer.
 */
public interface XMLStreamToXMLStreamTransformer {
	
	public void transform(XMLStreamReader reader, BufferedXMLStreamWriter writer) throws TransformerException;
	
}
