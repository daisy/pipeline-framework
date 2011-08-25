package org.daisy.converter.parser.stax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URISyntaxException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;

import org.daisy.common.xproc.XProcPipelineInfo;
import org.daisy.common.xproc.XProcPortInfo;
import org.daisy.pipeline.script.XProcOptionMetadata;
import org.daisy.pipeline.script.XProcOptionMetadata.Direction;
import org.daisy.pipeline.script.XProcPortMetadata;
import org.daisy.pipeline.script.XProcScript;
import org.junit.Before;
import org.junit.Test;

public class XProcScriptParserTest {
	XProcScript scp;

	@Before
	public void setUp() throws URISyntaxException {
		StaxXProcScriptParser parser = new StaxXProcScriptParser();
		parser.setFactory(XMLInputFactory.newInstance());
		scp = parser.parse(this.getClass().getClassLoader()
				.getResource("script.xpl").toURI());
	}

	@Test
	public void converterParsingTest() throws URISyntaxException {
		assertEquals("short description", scp.getName());
		assertEquals("detail description", scp.getDescription());
	}

	@Test
	public void testInputPort() {
		XProcPortMetadata port = scp.getPortMetadata("source");
		assertEquals("application/x-dtbook+xml", port.getMediaType());
		assertEquals("source port", port.getNiceName());

	}

	@Test
	public void testOption() {
		XProcOptionMetadata opt = scp.getOptionMetadata(new QName("option1"));
		assertEquals("anyDirURI", opt.getType());
		assertEquals(Direction.OUTPUT, opt.getDirection());
		assertEquals("Option 1", opt.getNiceName());
	}

	@Test
	public void testInfo() {
		// just a simple test to see if is correctly set
		XProcPipelineInfo info = scp.getXProcPipelineInfo();
		assertNotNull(info);
		XProcPortInfo port = info.getInputPort("source");
		assertEquals("source", port.getName());
		assertEquals(true, port.isPrimary());
		assertEquals(true, port.isSequence());
	}
}
