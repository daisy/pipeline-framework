package org.daisy.pipeline.xmlcatalog.stax;

import java.net.URI;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;

import junit.framework.Assert;

import org.daisy.pipeline.xmlcatalog.XmlCatalog;
import org.junit.Before;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * The Class XmlCatalogParserTest.
 */
public class XmlCatalogParserTest {

	/** The catalog uri. */
	URI catalogUri=null;

	/** The parser. */
	private StaxXmlCatalogParser parser;

	/** The catalog. */
	private XmlCatalog catalog;

	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		catalogUri=this.getClass().getClassLoader().getResource("catalog.xml").toURI();
		parser = new StaxXmlCatalogParser();
		parser.setFactory(XMLInputFactory.newInstance());
		catalog = parser.parse(catalogUri);
	}

	/**
	 * Test catalog.
	 */
	@Test
	public void testCatalog() {
		Map<String, URI> publicuris=catalog.getPublicMappings();
		Map<URI, URI> systemuris=catalog.getSystemIdMappings();
		Map<URI, URI> uriuris=catalog.getUriMappings();
		Assert.assertEquals(publicuris.size(), 2);
		Assert.assertEquals(systemuris.size(), 2);
		Assert.assertEquals(uriuris.size(), 3);

		Assert.assertEquals(publicuris.get("-//OASIS//DTD DocBook XML V4.1.2//EN"),URI.create("mybase/docbookx.dtd"));
		Assert.assertEquals(publicuris.get("-//OASIS//DTD DocBook MathML Module V1.0//EN"),URI.create("http://www.oasis-open.org/docbook/xml/mathml/1.0/dbmathml.dtd"));

		Assert.assertEquals(systemuris.get(URI.create("http://example.com/groupfile.xml")), URI.create("mybase/mysistemIdgroup.xml"));
		Assert.assertEquals(systemuris.get(URI.create("http://example.com/file.xml")), URI.create("mysistemId.xml"));

		Assert.assertEquals(uriuris.get(URI.create("http://example.com/groupuri.xml")), URI.create("mybase/myurigroup.xml"));
		Assert.assertEquals(uriuris.get(URI.create("http://example.com/groupuri2.xml")), URI.create("mybase/myurigroup2.xml"));
		Assert.assertEquals(uriuris.get(URI.create("http://example.com/uri.xml")), URI.create("myuri.xml"));

	}

}
