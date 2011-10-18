package org.daisy.common.xproc.calabash;

import java.io.IOException;
import java.net.URL;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

// TODO: Make a module containing the entities and wrap a uri resolver using this class
/**
 * This class prevents calabash downloading dtd and other entities from the internet. Also prevents crashes due to the impossibility of getting http related dtds
 */
public class CalabashXprocEntityResolver implements EntityResolver {

	/** The Constant SMIL_DTD. */
	private static final String SMIL_DTD = "resources/SMIL10.dtd";
	
	/** The Constant SMIL_PUBLIC. */
	private static final String SMIL_PUBLIC = "http://www.w3.org/TR/REC-smil/SMIL10.dtd";

	/* (non-Javadoc)
	 * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String, java.lang.String)
	 */
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
