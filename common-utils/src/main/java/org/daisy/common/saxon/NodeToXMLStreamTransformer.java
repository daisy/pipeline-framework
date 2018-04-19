package org.daisy.common.saxon;

import java.util.function.Supplier;
import java.util.Iterator;

import javax.xml.stream.XMLStreamWriter;

import org.daisy.common.transform.TransformerException;

import net.sf.saxon.s9api.XdmNode;

public interface NodeToXMLStreamTransformer {
	
	public void transform(Iterator<XdmNode> input, Supplier<XMLStreamWriter> output) throws TransformerException;
	
}