package org.daisy.pipeline.xproc;

import java.util.Queue;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;

import org.junit.Assert;
import org.junit.Test;

public class PortTest {

	@Test
	public void simpleInputPortTest(){
		InputPort inp = new InputPort("test");
		Source src= new SAXSource();
		src.setSystemId("http://test/text.xml");
		inp.addBind(src);
		Source res=inp.getBinds().peek();
		Assert.assertEquals(src.getSystemId(), res.getSystemId());  
	}
	@Test
	public void sequenceInputPortTest(){
		InputPort inp = new InputPort("test");
		Source src= new SAXSource();
		src.setSystemId("http://test/text.xml");
		inp.addBind(src);
		Source src2= new SAXSource();
		src2.setSystemId("http://test/text2.xml");
		inp.addBind(src2);
		Queue<Source> binds=inp.getBinds(); 
		Source res=binds.poll();
		Assert.assertEquals(src.getSystemId(), res.getSystemId());
		res=binds.poll();
		Assert.assertEquals(src2.getSystemId(), res.getSystemId());
	}
	@Test
	public void simpleOutputPortTest(){
		OutputPort outPorts = new OutputPort("test");
		Result in= new SAXResult();
		in.setSystemId("http://test/text.xml");
		outPorts.addBind(in);
		Result res=outPorts.getBinds().peek();
		Assert.assertEquals(in.getSystemId(), res.getSystemId());
	}
	@Test
	public void sequenceOutputPortTest(){
		OutputPort outPorts = new OutputPort("test");
		Result in= new SAXResult();
		in.setSystemId("http://test/text.xml");
		Result in2= new SAXResult();
		in2.setSystemId("http://test/text2.xml");
		
		outPorts.addBind(in);
		outPorts.addBind(in2);
		Queue<Result> res=outPorts.getBinds();
		
		Assert.assertEquals(in.getSystemId(), res.poll().getSystemId());
		Assert.assertEquals(in2.getSystemId(), res.poll().getSystemId());
	}
	
	@Test
	public void parameterPortTest(){
		ParameterPort pp = new ParameterPort("parameter");
		pp.addBind(new NamedValue("arg1", "val1"));
		pp.addBind(new NamedValue("arg2", "val2"));
		Queue<NamedValue> binds= pp.getBinds();
		Assert.assertEquals(binds.peek().getName(),"arg1");
		Assert.assertEquals(binds.poll().getValue(),"val1");
		Assert.assertEquals(binds.peek().getName(),"arg2");
		Assert.assertEquals(binds.poll().getValue(),"val2");
		
	}
}
