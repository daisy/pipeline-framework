package org.daisy.pipeline.xproc.impl;

import java.io.IOException;
import java.net.URL;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XprocEntityResolver implements EntityResolver {

	private static final String SMIL_DTD = "resources/SMIL10.dtd";
	private static final String SMIL_PUBLIC = "http://www.w3.org/TR/REC-smil/SMIL10.dtd";

	@Override
	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException, IOException {
		if (systemId.equalsIgnoreCase(SMIL_PUBLIC)) {
			URL url = this.getClass().getClassLoader().getResource(SMIL_DTD);
			return new InputSource(url.openStream());
		}else if (systemId.contains("xhtml-lat1.ent")) {
			URL url = this.getClass().getClassLoader().getResource("resources/xhtml-lat1.ent");
			return new InputSource(url.openStream());
		}else if (systemId.contains("xhtml-special.ent")) {
			URL url = this.getClass().getClassLoader().getResource("resources/xhtml-special.ent");
			return new InputSource(url.openStream());
		}else if (systemId.contains("xhtml-symbol.ent")) {
			URL url = this.getClass().getClassLoader().getResource("resources/xhtml-symbol.ent");
			return new InputSource(url.openStream());
			
		}else if (systemId.equals("http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd")) {
			URL url = this.getClass().getClassLoader().getResource("resources/xhtml1-frameset.dtd");
			return new InputSource(url.openStream());
		}else if (systemId.equals("http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd")) {
			URL url = this.getClass().getClassLoader().getResource("resources/xhtml1-strict.dtd");
			return new InputSource(url.openStream());
		}else if (systemId.equals("http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd")) {
			URL url = this.getClass().getClassLoader().getResource("resources/xhtml1-transitional.dtd");
			return new InputSource(url.openStream());
		}else if (systemId.equals("http://www.daisy.org/z3986/2005/dtbook-2005-3.dtd")) {
			URL url = this.getClass().getClassLoader().getResource("resources/dtbook-2005-3.dtd");
			return new InputSource(url.openStream());
			
		}else if (systemId.equals("http://www.daisy.org/z3986/2005/dtbook-2005-2.dtd")) {
			URL url = this.getClass().getClassLoader().getResource("resources/dtbook-2005-2.dtd");
			return new InputSource(url.openStream());
		}else if (systemId.equals("http://www.daisy.org/z3986/2005/dtbook-2005-1.dtd")){
			URL url = this.getClass().getClassLoader().getResource("resources/dtbook-2005-1.dtd");
			return new InputSource(url.openStream());
		}else {
			// use the default behaviour
			return null;
		}
	}

}
