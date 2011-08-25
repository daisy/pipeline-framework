package org.daisy.converter.parser.stax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URISyntaxException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;

import org.daisy.common.xproc.XProcOptionInfo;
import org.daisy.common.xproc.XProcPipelineInfo;
import org.daisy.common.xproc.XProcPortInfo;
import org.junit.Before;
import org.junit.Test;

public class XProcPipelineInfoParserTest {

	private XProcPipelineInfo xproc;

	@Before
	public void setUp() throws URISyntaxException {
		StaxXProcPipelineInfoParser parser = new StaxXProcPipelineInfoParser();
		parser.setFactory(XMLInputFactory.newInstance());
		xproc = parser.parse(this.getClass().getClassLoader()
				.getResource("script.xpl").toURI());
	}

	@Test
	public void testInputPort() {
		XProcPortInfo port = xproc.getInputPort("source");
		assertEquals("source", port.getName());
		assertEquals(true, port.isPrimary());
		assertEquals(true, port.isSequence());

	}

	@Test
	public void testOption() {
		XProcOptionInfo info = xproc.getOption(new QName("option1"));
		assertNotNull(info);
		assertEquals(".", info.getSelect());
		assertEquals(true, info.isRequired());
	}
}
