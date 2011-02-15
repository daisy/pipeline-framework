package org.daisy.common.stax;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public interface EventProcessor {
	void process(XMLEvent event) throws XMLStreamException;
}
