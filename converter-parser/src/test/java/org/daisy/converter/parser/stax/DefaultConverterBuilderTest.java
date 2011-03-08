package org.daisy.converter.parser.stax;
/*
import junit.framework.Assert;

import org.daisy.converter.parser.ConverterBuilder;
import org.daisy.converter.parser.ConverterBuilder.ConverterArgumentBuilder;
import org.daisy.converter.parser.DefaultConverterBuilder;
import org.daisy.pipeline.modules.converter.Converter;
import org.daisy.pipeline.modules.converter.Converter.ConverterArgument;
import org.daisy.pipeline.modules.converter.Converter.ConverterArgument.Type;
import org.junit.Before;
import org.junit.Test;
import org.daisy.converter.registry.OSGIConverter;
public class DefaultConverterBuilderTest {
	private static final String VERSION = "version";
	private static final String NAME = "name";
	private static final String DESC = "desc";
	private static String A_NAME = "argument";
	private static String A_DESC = "arg_desc";
	private static String A_BIND = "arg_bind";
	private static String A_PORT = "arg_port";
	ConverterArgumentBuilder cab;
	ConverterBuilder cb;
	@Before
	public void setup(){
		cab = new DefaultConverterBuilder(new OSGIConverter())
		.getConverterArgumentBuilder();
		cb =new DefaultConverterBuilder(new OSGIConverter());
	}
	
	@Test
	public void testSimpleArgument() {
		
		ConverterArgument arg = cab.withName(A_NAME).withDesc(A_DESC)
				.withBind(A_BIND).withPort(A_PORT).build();
		
		Assert.assertEquals(A_NAME, arg.getName());
		Assert.assertEquals(A_DESC, arg.getDesc());
		Assert.assertEquals(A_BIND, arg.getBind());
		Assert.assertEquals(A_PORT, arg.getPort());
		
	}
	@Test
	public void testTypeArgument() {
				
		try{
			cab.withType("other");
			Assert.fail("Expecting exception");
		}catch (IllegalArgumentException e) {}
		
		ConverterArgument input = cab.withType("input").build();	
		ConverterArgument output = cab.withType("output").build();
		ConverterArgument option = cab.withType("option").build();
		ConverterArgument parameter = cab.withType("parameter").build();
		
		
		Assert.assertEquals(Type.INPUT, input.getType());
		Assert.assertEquals(Type.OUTPUT, output.getType());
		Assert.assertEquals(Type.OPTION, option.getType());
		Assert.assertEquals(Type.PARAMETER, parameter.getType());
		
		
		
	}
	@Test 
	public void testOptional(){
		try{
			cab.withOptional("maybe");
			Assert.fail("Expecting exception");
		}catch (IllegalArgumentException e) {}
		
		ConverterArgument opt = cab.withOptional("true").build();	
		ConverterArgument notOpt = cab.withOptional("false").build();
		Assert.assertEquals(true, opt.isOptional());
		Assert.assertEquals(false, notOpt.isOptional());
	}
	
	@Test
	public void testConverterBuilder(){
		Converter conv = cb.withDescription(DESC).withName(NAME).withVersion(VERSION).build();
		Assert.assertEquals(NAME, conv.getName());
		Assert.assertEquals(DESC, conv.getDescription());
		Assert.assertEquals(VERSION, conv.getVersion());
	}
	@Test
	public void testConverterWithArg(){
		 cab.withName(A_NAME).withDesc(A_DESC).withBind(A_BIND).withPort(A_PORT);
		
		Converter conv = cb.withDescription(DESC).withName(NAME).withVersion(VERSION).withArgument(cab).build();
		ConverterArgument arg = conv.getArgument(A_NAME);
		
		Assert.assertEquals(A_NAME, arg.getName());
		Assert.assertEquals(A_DESC, arg.getDesc());
		Assert.assertEquals(A_BIND, arg.getBind());
		Assert.assertEquals(A_PORT, arg.getPort());
		Assert.assertEquals(NAME, conv.getName());
		Assert.assertEquals(DESC, conv.getDescription());
		Assert.assertEquals(VERSION, conv.getVersion());
	}
}
*/