package org.daisy.converter.parser.stax;

import java.net.URI;

import junit.framework.Assert;

import org.daisy.converter.parser.ConverterBuilder;
import org.daisy.converter.parser.ConverterBuilder.ConverterArgumentBuilder;
import org.daisy.converter.parser.DefaultConverterBuilder;
import org.daisy.pipeline.modules.converter.Converter;
import org.daisy.pipeline.modules.converter.ConverterArgument;
import org.daisy.pipeline.modules.converter.ConverterArgument.BindType;
import org.daisy.pipeline.modules.converter.ConverterArgument.Direction;
import org.daisy.pipeline.modules.converter.ConverterArgument.OutputType;
import org.junit.Before;
import org.junit.Test;

public class DefaultConverterBuilderTest {
	private static final String VERSION = "version";
	private static final String NAME = "name";
	private static final String DESC = "desc";
	private static String A_NAME = "argument";
	private static String A_DESC = "arg_desc";
	private static String A_BIND = "arg_bind";
	private static String A_PORT = "arg_port";
	private static String A_MEDIA = "arg_media";
	ConverterArgumentBuilder cab;
	ConverterBuilder cb;
	@Before
	public void setup(){
		cab = new DefaultConverterBuilder(new MockConverter.MockFactory())
		.getConverterArgumentBuilder();
		cb =new DefaultConverterBuilder(new MockConverter.MockFactory());
	}
	
	@Test
	public void testSimpleArgument() {
		ConverterArgument arg = cab.withName(A_NAME).withDesc(A_DESC)
				.withBind(A_BIND).withPort(A_PORT).withMediaType(A_MEDIA).build();
		
		Assert.assertEquals(A_NAME, arg.getName());
		Assert.assertEquals(A_DESC, arg.getDesc());
		Assert.assertEquals(A_BIND, arg.getBind());
		Assert.assertEquals(A_MEDIA, arg.getMediaType());
		//Assert.assertEquals(A_PORT, arg.getPort());
		
	}
	@Test
	public void testBindTypeArgument() {
				
		try{
			cab.withBindType("other");
			Assert.fail("Expecting exception");
		}catch (IllegalArgumentException e) {}
		
		ConverterArgument input = cab.withBindType("port").build();	
		ConverterArgument output = cab.withBindType("port").build();
		ConverterArgument option = cab.withBindType("option").build();
		ConverterArgument parameter = cab.withBindType("parameter").build();
		
		
		Assert.assertEquals(BindType.PORT, input.getBindType());
		Assert.assertEquals(BindType.PORT, output.getBindType());
		Assert.assertEquals(BindType.OPTION, option.getBindType());
		Assert.assertEquals(BindType.PARAMETER, parameter.getBindType());
		
		
		
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
	public void testSequence(){
		try{
			cab.withSequence("maybe");
			Assert.fail("Expecting exception");
		}catch (IllegalArgumentException e) {}
		ConverterArgument seq = cab.withSequence("true").build();	
		ConverterArgument notSeq = cab.withSequence("false").build();
		Assert.assertEquals(true, seq.isSequence());
		Assert.assertEquals(false, notSeq.isSequence());
	}
	
	@Test
	public void testOutputTypeArgument() {
				
		try{
			cab.withType("other");
			Assert.fail("Expecting exception");
		}catch (IllegalArgumentException e) {}
		
		ConverterArgument input = cab.withType("anyFileURI").build();	
		ConverterArgument output = cab.withType("anyFolderURI").build();
		
		
		
		Assert.assertEquals(OutputType.FILE, input.getOutputType());
		Assert.assertEquals(OutputType.FOLDER, output.getOutputType());
		
		
		
		
	}
	
	@Test
	public void testDirArgument() {
				
		try{
			cab.withDir("other");
			Assert.fail("Expecting exception");
		}catch (IllegalArgumentException e) {}
		
		ConverterArgument input = cab.withDir("input").build();	
		ConverterArgument output = cab.withDir("output").build();
		
		
		
		Assert.assertEquals(Direction.INPUT, input.getDirection());
		Assert.assertEquals(Direction.OUTPUT, output.getDirection());
		
		
		
		
	}
	

	
	@Test
	public void testConverterBuilder(){
		URI uri= URI.create("http://test.com");
		Converter conv = cb.withDescription(DESC).withName(NAME).withVersion(VERSION).withURI(uri).build();
		Assert.assertEquals(NAME, conv.getName());
		Assert.assertEquals(DESC, conv.getDescription());
		Assert.assertEquals(VERSION, conv.getVersion());
		Assert.assertEquals(uri, conv.getURI());
	}
	@Test
	public void testConverterWithArg(){
		 cab.withName(A_NAME).withDesc(A_DESC).withBind(A_BIND).withPort(A_PORT);
		
		Converter conv = cb.withDescription(DESC).withName(NAME).withVersion(VERSION).withArgument(cab).build();
		ConverterArgument arg = conv.getArgument(A_NAME);
		
		Assert.assertEquals(A_NAME, arg.getName());
		Assert.assertEquals(A_DESC, arg.getDesc());
		Assert.assertEquals(A_BIND, arg.getBind());
		Assert.assertEquals(NAME, conv.getName());
		Assert.assertEquals(DESC, conv.getDescription());
		Assert.assertEquals(VERSION, conv.getVersion());
	}
}
