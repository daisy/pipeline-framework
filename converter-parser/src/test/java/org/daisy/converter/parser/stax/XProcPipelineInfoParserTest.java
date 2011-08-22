package org.daisy.converter.parser.stax;

import java.net.URISyntaxException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;

import junit.framework.Assert;

import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.osgi.Stax2InputFactoryProvider;
import org.daisy.common.xproc.XProcOptionInfo;
import org.daisy.common.xproc.XProcPipelineInfo;
import org.daisy.common.xproc.XProcPortInfo;
import org.daisy.pipeline.script.XProcOptionMetadata;
import org.daisy.pipeline.script.XProcOptionMetadata.Direction;
import org.daisy.pipeline.script.XProcPortMetadata;
import org.daisy.pipeline.script.XProcScript;
import org.junit.Before;
import org.junit.Test;

public class XProcPipelineInfoParserTest {
	XProcPipelineInfo scp;

	@Before
	public void setUp() throws URISyntaxException {
		StaxXProcPipelineInfoParser parser = new StaxXProcPipelineInfoParser();
		parser.setFactory(XMLInputFactory.newInstance());
		XProcPipelineInfo.Builder builder = new XProcPipelineInfo.Builder();
		builder.withURI(this.getClass().getClassLoader()
				.getResource("converterDescriptor.xpl").toURI());
		scp = parser.parse(this.getClass().getClassLoader()
				.getResource("converterDescriptor.xpl").toURI());
	}

	
	
	@Test
	public void testInputPort(){
		XProcPortInfo port=scp.getInputPort("source");
		Assert.assertEquals("source", port.getName());
		Assert.assertEquals(true, port.isPrimary());
		Assert.assertEquals(true, port.isSequence());
		
	}
	@Test
	public void testOption(){
		XProcOptionInfo info =scp.getOption(new QName("opt-output-dir"));
		Assert.assertEquals(".", info.getSelect());
		Assert.assertEquals(true, info.isRequired());
	}
}
