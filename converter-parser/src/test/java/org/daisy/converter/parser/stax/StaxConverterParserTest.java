package org.daisy.converter.parser.stax;
/*
import java.net.URISyntaxException;

import javax.xml.stream.XMLInputFactory;

import junit.framework.Assert;

import org.daisy.converter.parser.ConverterBuilder;
import org.daisy.converter.parser.DefaultConverterBuilder;
import org.daisy.converter.registry.OSGIConverter;
import org.daisy.converter.registry.OSGIConverterDescriptor;
import org.daisy.pipeline.modules.converter.Converter;
import org.daisy.pipeline.modules.converter.Converter.ConverterArgument;
import org.daisy.pipeline.modules.converter.Converter.ConverterArgument.Type;
import org.daisy.pipeline.modules.converter.ConverterDescriptor;
import org.junit.Before;
import org.junit.Test;


public class StaxConverterParserTest {
	Converter conv;
	@Before
	public void setUp() throws URISyntaxException{
		StaxConverterParser parser = new StaxConverterParser();
		parser.setDirectFactory(XMLInputFactory.newInstance());
		ConverterDescriptor desc = new OSGIConverterDescriptor();
		desc.setFile(this.getClass().getClassLoader().getResource("converterDescriptor.xpl").toURI());
		ConverterBuilder cb =new DefaultConverterBuilder(new OSGIConverter());
		conv=parser.parse(desc, cb);
	}
	@Test
	public void converterParsingTest() throws URISyntaxException{

		Assert.assertEquals("testHello", conv.getName());
		Assert.assertEquals("1.0", conv.getVersion());
		Assert.assertEquals(" Test xpl description", conv.getDescription());
	}
	
	@Test
	public void converterArgumentsParsingTest() throws URISyntaxException{

		ConverterArgument arg = conv.getArgument("in");
		
		Assert.assertEquals("input for hello process", arg.getDesc());
		Assert.assertEquals(Type.INPUT, arg.getType());
		Assert.assertEquals("source", arg.getPort());
		Assert.assertEquals(true, arg.isOptional());
		
		arg = conv.getArgument("out");
		Assert.assertEquals("the result file", arg.getDesc());
		Assert.assertEquals(Type.OUTPUT, arg.getType());
		Assert.assertEquals("result", arg.getPort());
		Assert.assertEquals(false, arg.isOptional());
		
		arg = conv.getArgument("o");
		Assert.assertEquals("that kind of option that modifies the converter behaviour", arg.getDesc());
		Assert.assertEquals(Type.OPTION, arg.getType());
		Assert.assertEquals("opt", arg.getBind());
		Assert.assertEquals(false, arg.isOptional());
		
		arg = conv.getArgument("msg");
		Assert.assertEquals("msg to show", arg.getDesc());
		Assert.assertEquals(Type.PARAMETER, arg.getType());
		Assert.assertEquals("msg", arg.getBind());
		Assert.assertEquals("params", arg.getPort());
		Assert.assertEquals(false, arg.isOptional());
		
	}
}
*/