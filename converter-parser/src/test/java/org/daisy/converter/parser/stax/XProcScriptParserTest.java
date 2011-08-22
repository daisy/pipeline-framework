package org.daisy.converter.parser.stax;

import java.net.URISyntaxException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;

import junit.framework.Assert;

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
		XProcPipelineInfo.Builder builder = new XProcPipelineInfo.Builder();
		builder.withURI(this.getClass().getClassLoader()
				.getResource("converterDescriptor.xpl").toURI());
		scp = parser.parse(builder.build().getURI());
	}

	@Test
	public void converterParsingTest() throws URISyntaxException {
		Assert.assertEquals(
				"Main entry point for DTBook-to-ZedAI. Transforms DTBook XML into ZedAI XML, and\n            extracts metadata and CSS information into separate files. ",
				scp.getName());
		Assert.assertEquals("TODO: can we have an xd:element for component home pages? http://code.google.com/p/daisy-pipeline/wiki/DTBookToZedAI", scp.getDescription());
	}
	@Test
	public void testInputPort(){
		XProcPortMetadata port=scp.getPortMetadata("source");
		Assert.assertEquals("application/x-dtbook+xml", port.getMediaType());
		Assert.assertEquals("DTBook input file(s)", port.getNiceName());
		
	}
	@Test
	public void testOption(){
		XProcOptionMetadata opt=scp.getOptionMetadata(new QName("opt-output-dir"));
		Assert.assertEquals("anyDirURI", opt.getType());
		Assert.assertEquals(Direction.OUTPUT, opt.getDirection());
		Assert.assertEquals("Output directory", opt.getNiceName());
	}
	@Test
	public void testInfo(){
		//just a simple test to see if is correctly set
		XProcPipelineInfo info =scp.getXProcPipelineInfo();
		XProcPortInfo port=info.getInputPort("source");
		Assert.assertEquals("source", port.getName());
		Assert.assertEquals(true, port.isPrimary());
		Assert.assertEquals(true, port.isSequence());
	}
}
