package org.daisy.pipeline.webservice.xml;

import org.daisy.common.properties.Properties.Property;
import org.daisy.pipeline.webservice.Routes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PropertyXmlWriter {

	private static Logger logger = LoggerFactory.getLogger(PropertyXmlWriter.class);

	private final String baseUrl;
	private final Property property;

	public PropertyXmlWriter(Property property, String baseUrl) {
		this.property = property;
		this.baseUrl = baseUrl;
	}

	public Document getXmlDocument() {
		Document doc = XmlUtils.createDom("property");
		addElementData(doc.getDocumentElement());
		// for debugging only
		if (!XmlValidator.validate(doc, XmlValidator.PROPERTY_SCHEMA_URL)) {
			logger.error("INVALID XML:\n" + XmlUtils.nodeToString(doc));
		}
		return doc;
	}

	// instead of creating a standalone XML document, add an element to an existing document
	public void addAsElementChild(Element parent) {
		Document doc = parent.getOwnerDocument();
		Element propertyElm = doc.createElementNS(XmlUtils.NS_PIPELINE_DATA, "property");
		addElementData(propertyElm);
		parent.appendChild(propertyElm);
	}

	private void addElementData(Element element) {
		element.setAttribute("href", baseUrl + Routes.PROPERTY_ROUTE.replaceFirst("\\{name\\}", property.getName()));
		element.setAttribute("name", property.getName());
		String val = property.getValue();
		if (val != null)
			element.setAttribute("value", val);
	}
}
