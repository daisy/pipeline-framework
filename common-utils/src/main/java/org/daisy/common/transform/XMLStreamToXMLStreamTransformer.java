package org.daisy.common.transform;

import java.util.function.Supplier;
import java.util.Iterator;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/*
 * Note that this interface does not extend javax.xml.transform.Transformer.
 */
public interface XMLStreamToXMLStreamTransformer {
	
	/**
	 * Transform a sequence of input documents to a sequence of output documents.
	 *
	 * @param input A sequence of XMLStreamReaders. Allowed to throw TransformerException.
	 * @param output A supplier of XMLStreamWriters. Allowed to throw TransformerException.
	 * @throws TransformerException
	 */
	public void transform(Iterator<XMLStreamReader> input, Supplier<XMLStreamWriter> output) throws TransformerException;
	
}
