package org.daisy.converter.parser.stax;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
	public void testDescription() throws URISyntaxException {
		assertEquals("short description", scp.getName());
		assertEquals("detail description", scp.getDescription());
		assertEquals("homepage", scp.getHomepage());
	}

	@Test
	public void testPortMetadataIsSet() {
		for (XProcPortInfo port : scp.getXProcPipelineInfo().getInputPorts()) {
			assertNotNull("port '" + port.getName() + "' has no metadata",
					scp.getPortMetadata(port.getName()));
		}
		for (XProcPortInfo port : scp.getXProcPipelineInfo().getOutputPorts()) {
			assertNotNull("port '" + port.getName() + "' has no metadata",
					scp.getPortMetadata(port.getName()));
		}
		//TODO test parameter ports
	}

	@Test
	public void testInputPort() {
		XProcPortMetadata port = scp.getPortMetadata("source");
		assertEquals("application/x-dtbook+xml", port.getMediaType());
		assertEquals("source name", port.getNiceName());
		assertEquals("source description", port.getDescription());

	}

	@Test
	public void testMissingInputMetadata() {
		XProcPortMetadata meta = scp.getPortMetadata("source2");
		assertNotNull(meta);
		assertNull(meta.getMediaType());
		assertNull(meta.getNiceName());
		assertNull(meta.getDescription());
	}
	
	@Test
	public void testOutputPort() {
		XProcPortMetadata port = scp.getPortMetadata("result");
		port.getDescription();
		assertEquals("application/x-dtbook+xml", port.getMediaType());
		assertEquals("result name", port.getNiceName());
		assertEquals("result description", port.getDescription());

	}

	@Test
	public void testMissingOutputMetadata() {
		XProcPortMetadata meta = scp.getPortMetadata("result2");
		assertNotNull(meta);
		assertNull(meta.getMediaType());
		assertNull(meta.getNiceName());
		assertNull(meta.getDescription());
	}

	@Test
	public void testParameterPort() {
		// TODO test parameter metadata
	}
	
	@Test
	public void testMissingParameterMetadata() {
		// TODO test missing parameter metadata
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
		assertTrue(port.isPrimary());
		assertTrue(port.isSequence());
	}
}
