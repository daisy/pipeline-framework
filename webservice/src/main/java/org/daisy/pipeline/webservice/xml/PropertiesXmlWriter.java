package org.daisy.pipeline.webservice.xml;

import java.util.List;

import org.daisy.common.properties.Properties.Property;
import org.daisy.pipeline.webservice.Routes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PropertiesXmlWriter {

	private static Logger logger = LoggerFactory.getLogger(PropertiesXmlWriter.class.getName());

	private final String baseUrl;
	private final List<? extends Property> properties;

	public PropertiesXmlWriter(List<? extends Property> properties, String baseUrl) {
		this.properties = properties;
		this.baseUrl = baseUrl;
	}

	public Document getXmlDocument() {
		Document doc = XmlUtils.createDom("properties");
		Element propsElm = doc.getDocumentElement();
		propsElm.setAttribute("href", baseUrl + Routes.PROPERTIES_ROUTE);
		for (Property p : properties) {
			PropertyXmlWriter writer = new PropertyXmlWriter(p, baseUrl);
			writer.addAsElementChild(propsElm);
		}
		// for debugging only
		if (!XmlValidator.validate(doc, XmlValidator.PROPERTIES_SCHEMA_URL)) {
			logger.error("INVALID XML:\n" + XmlUtils.nodeToString(doc));
		}
		return doc;
	}
}
