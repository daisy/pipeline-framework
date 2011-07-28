package org.daisy.pipeline.modules.converter;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;

import org.daisy.pipeline.modules.converter.ConverterArgument.BindType;
import org.daisy.pipeline.modules.converter.ConverterArgument.Direction;
import org.daisy.pipeline.modules.converter.ConverterArgument.ValuedConverterArgument;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConverterArgumentTest {
	private static final String HTTP_TEST_COM_IN2 = "http://test.com/in2";
	private static final String HTTP_TEST_COM_IN = "http://test.com/in";
	ConverterArgument mInput;
	ConverterArgument mOutput;
	ConverterArgument mOption;
	@Before
	public void setUp() throws Exception {
		mInput= new ConverterArgument("input", BindType.PORT, "source", Direction.INPUT, "unk", "test in", false,null,false);
		mOutput= new ConverterArgument("output", BindType.PORT, "result", Direction.OUTPUT, "unk", "test out", false,null,false);
		mOption= new ConverterArgument("opt", BindType.OPTION, "option", null, "unk", "test option", false,null,false);
	}
	@Test
	public void testValuedArgumentInputSimple(){
		SAXSource src= new SAXSource();
		src.setSystemId(HTTP_TEST_COM_IN);
		ValuedConverterArgument<Source> val=mInput.getValuedConverterBuilder().withSource(src);
		Assert.assertEquals(HTTP_TEST_COM_IN,val.getValues().get(0).getSystemId());
		
		
	}
	@Test
	public void testValuedArgumentOutputSimple(){
		SAXResult res= new SAXResult();
		res.setSystemId(HTTP_TEST_COM_IN);
		ValuedConverterArgument<Result> val=mOutput.getValuedConverterBuilder().withResult(res);
		Assert.assertEquals(HTTP_TEST_COM_IN,val.getValues().get(0).getSystemId());
	}
	
	@Test 
	public void testValuedArgumentInputErr(){
		SAXResult res= new SAXResult();
		res.setSystemId(HTTP_TEST_COM_IN);
		try{
			ValuedConverterArgument<Result> val=mInput.getValuedConverterBuilder().withResult(res);
			Assert.fail();
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	@Test 
	public void testValuedArgumentOutputErr(){
		SAXSource src= new SAXSource();
		src.setSystemId(HTTP_TEST_COM_IN);

		try{
			ValuedConverterArgument<Source> val=mOutput.getValuedConverterBuilder().withSource(src);
			Assert.fail();
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	@Test
	public void testValuedArgumentInputSequence(){
		SAXSource src= new SAXSource();
		SAXSource src2= new SAXSource();
		src.setSystemId(HTTP_TEST_COM_IN);
		src2.setSystemId(HTTP_TEST_COM_IN2);
		
		ValuedConverterArgument<Source> val=mInput.getValuedConverterBuilder().withSource(src,src2);
		Assert.assertEquals(HTTP_TEST_COM_IN,val.getValues().get(0).getSystemId());
		Assert.assertEquals(HTTP_TEST_COM_IN2,val.getValues().get(1).getSystemId());
		
		
	}
	
	@Test
	public void testOption(){
			
		ValuedConverterArgument<String> val=mOption.getValuedConverterBuilder().withString(HTTP_TEST_COM_IN);
		Assert.assertEquals(HTTP_TEST_COM_IN,val.getValues().get(0));
				
	}
	@Test
	public void testOptionErr(){
		SAXSource src= new SAXSource();
		src.setSystemId(HTTP_TEST_COM_IN2);
		try{
			ValuedConverterArgument<Source> val=mOption.getValuedConverterBuilder().withSource(src);
			Assert.fail();
		}catch (Exception e) {
				
		}
		
				
	}
}


