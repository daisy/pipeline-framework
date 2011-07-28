package org.daisy.converter.parser.stax;

import java.net.URISyntaxException;

import javax.xml.stream.XMLInputFactory;

import junit.framework.Assert;

import org.daisy.converter.parser.ConverterBuilder;
import org.daisy.converter.parser.DefaultConverterBuilder;
import org.daisy.pipeline.modules.converter.Converter;
import org.daisy.pipeline.modules.converter.ConverterArgument;
import org.daisy.pipeline.modules.converter.ConverterArgument.BindType;
import org.daisy.pipeline.modules.converter.ConverterArgument.Direction;
import org.daisy.pipeline.modules.converter.ConverterArgument.OutputType;
import org.daisy.pipeline.modules.converter.ConverterDescriptor;
import org.junit.Before;
import org.junit.Test;


public class StaxConverterParserTest {
	Converter conv;
	@Before
	public void setUp() throws URISyntaxException{
		StaxConverterParser parser = new StaxConverterParser();
		parser.setDirectFactory(XMLInputFactory.newInstance());
		ConverterDescriptor desc = new ConverterDescriptor() {
		};
		desc.setFile(this.getClass().getClassLoader().getResource("converterDescriptor.xpl").toURI());
		ConverterBuilder cb =new DefaultConverterBuilder(new MockConverter.MockFactory());
		conv=parser.parse(desc, cb);
	}
	@Test
	public void converterParsingTest() throws URISyntaxException{

		Assert.assertEquals("dtbook-to-zedai", conv.getName());
		Assert.assertEquals("1.0", conv.getVersion());
		Assert.assertEquals("Convert DTBook XML to ZedAI XML", conv.getDescription());
	}
	
	@Test
	public void converterArgumentsParsingTest() throws URISyntaxException{

		ConverterArgument arg = conv.getArgument("in");
		
		Assert.assertEquals("DTBook input file(s)", arg.getDesc());
		Assert.assertEquals(BindType.PORT, arg.getBindType());
		Assert.assertEquals(false, arg.isOptional());
		Assert.assertEquals("source", arg.getBind());
		Assert.assertEquals(true, arg.isSequence());
		Assert.assertEquals("application/x-dtbook+xml", arg.getMediaType());
		Assert.assertEquals(Direction.INPUT,arg.getDirection() );
		
		
		arg = conv.getArgument("o");
		Assert.assertEquals("Output file path", arg.getDesc());
		Assert.assertEquals(BindType.OPTION, arg.getBindType());
		Assert.assertEquals("output-file", arg.getBind());
		Assert.assertEquals(false, arg.isOptional());
		Assert.assertEquals(Direction.OUTPUT,arg.getDirection() );
		Assert.assertEquals(OutputType.FILE,arg.getOutputType());
		Assert.assertEquals("application/z3986-auth+xml", arg.getMediaType());
		
	}
	
}
