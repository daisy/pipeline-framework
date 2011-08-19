package org.daisy.pipeline.modules.converter;


import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Queue;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.xpath.XPathException;

import org.daisy.pipeline.modules.converter.ConverterArgument.BindType;
import org.daisy.pipeline.modules.converter.ConverterArgument.Direction;
import org.daisy.pipeline.xproc.InputPort;
import org.daisy.pipeline.xproc.NamedValue;
import org.daisy.pipeline.xproc.OutputPort;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ConverterRunnableTest extends ConverterRunnable{
	private static final String HTTP_TEST_COM_IN2 = "http://test.com/in2";
	private static final String HTTP_TEST_COM_IN = "http://test.com/in";
	public ConverterRunnableTest() {
		super(new MyConverter());
		// TODO Auto-generated constructor stub
	}
	ConverterRunnable mRunner;
	private ConverterArgument mOption;
	private ConverterArgument mOutput;
	private ConverterArgument mInput;
	private Bundle mBundle;
	private Executor mExecutor;
	@Before
	public void setUp() throws Exception {
		mBundle = FrameworkUtil.getBundle(this.getClass());
		ServiceReference serv=mBundle.getBundleContext().getServiceReference(Executor.class.getCanonicalName());
		mExecutor = (Executor)mBundle.getBundleContext().getService(serv);
		
		MyConverter conv = new MyConverter();
		conv.uri = this.getClass().getClassLoader().getResource("option.xpl").toURI();
		HashMap<String, ConverterArgument> args= new HashMap<String, ConverterArgument>();
		conv.args=args;
		mInput = new ConverterArgument("input", BindType.PORT, "source", Direction.INPUT, "unk", "test in", false,null,false);
		mOutput = new ConverterArgument("output", BindType.PORT, "result", Direction.OUTPUT, "unk", "test out", false,null,false);
		mOption = new ConverterArgument("opt", BindType.OPTION, "opt", null, "unk", "test option", false,null,false);
		args.put(mInput.getName(),mInput);
		args.put(mOutput.getName(),mOutput);
		args.put(mOption.getName(),mOption);
		mRunner = new ConverterRunnable(conv);
		mRunner.setExecutor(mExecutor);
	}
	
	@Test
	public void testNoBinds(){
		try{
			mRunner.run();
			Assert.fail();
		}catch (RuntimeException e) {
			
		}
	}
	
	@Test
	public void testBinds(){
		SAXResult res= new SAXResult();
		SAXSource src= new SAXSource();
		src.setSystemId(HTTP_TEST_COM_IN);
		res.setSystemId(HTTP_TEST_COM_IN2);
		ConverterArgument in = mRunner.getConverter().getArgument("input");
		ConverterArgument out = mRunner.getConverter().getArgument("output");
		ConverterArgument opt = mRunner.getConverter().getArgument("opt");
		mRunner.setConverterArgumentValue(in.getValuedConverterBuilder().withSource(src));
		mRunner.setConverterArgumentValue(out.getValuedConverterBuilder().withResult(res));
		mRunner.setConverterArgumentValue(opt.getValuedConverterBuilder().withString("value"));
		mRunner.bind();
		InputPort ip = mRunner.getInputPorts().iterator().next();
		OutputPort op = mRunner.getOutputPorts().iterator().next();
		NamedValue nv = mRunner.getOptions().iterator().next();
		Assert.assertEquals(HTTP_TEST_COM_IN,ip.getBinds().peek().getSystemId());
		Assert.assertEquals(HTTP_TEST_COM_IN2,op.getBinds().peek().getSystemId());
		Assert.assertEquals("value",nv.getValue());
		
		
	}
	
	@Test
	public void testSequenceBinds(){
		SAXResult res= new SAXResult();
		SAXSource src= new SAXSource();
		SAXSource src1= new SAXSource();
		src.setSystemId(HTTP_TEST_COM_IN);
		src1.setSystemId(HTTP_TEST_COM_IN2);
		res.setSystemId(HTTP_TEST_COM_IN2);
		ConverterArgument in = mRunner.getConverter().getArgument("input");
		ConverterArgument out = mRunner.getConverter().getArgument("output");
		ConverterArgument opt = mRunner.getConverter().getArgument("opt");
		mRunner.setConverterArgumentValue(in.getValuedConverterBuilder().withSource(src,src1));
		mRunner.setConverterArgumentValue(out.getValuedConverterBuilder().withResult(res));
		mRunner.setConverterArgumentValue(opt.getValuedConverterBuilder().withString("value"));
		mRunner.bind();
		InputPort ip = mRunner.getInputPorts().iterator().next();
		OutputPort op = mRunner.getOutputPorts().iterator().next();
		NamedValue nv = mRunner.getOptions().iterator().next();
		Queue<Source> q = ip.getBinds();
		Assert.assertEquals(HTTP_TEST_COM_IN,q.poll().getSystemId());
		Assert.assertEquals(HTTP_TEST_COM_IN2,q.peek().getSystemId());
		Assert.assertEquals(HTTP_TEST_COM_IN2,op.getBinds().peek().getSystemId());
		Assert.assertEquals("value",nv.getValue());
		
		
	}
	
	@Test
	public void runTest() throws IOException, IllegalArgumentException, XPathException, SAXException, ParserConfigurationException{
		SAXResult res= new SAXResult();
		res.setSystemId(File.createTempFile("dp2", ".xml").toURI().toString());
		SAXSource src = new SAXSource();
		src.setSystemId(this.getClass().getClassLoader().getResource("doc.xml").toString());
		ConverterArgument in = mRunner.getConverter().getArgument("input");
		ConverterArgument out = mRunner.getConverter().getArgument("output");
		ConverterArgument opt = mRunner.getConverter().getArgument("opt");
		mRunner.setConverterArgumentValue(in.getValuedConverterBuilder().withSource(src));
		mRunner.setConverterArgumentValue(out.getValuedConverterBuilder().withResult(res));
		mRunner.setConverterArgumentValue(opt.getValuedConverterBuilder().withString("cosa"));
		mRunner.run();
		XmlDocument doc = new XmlDocument();
		doc.setValidating(false);
		doc.loadFromFile(URI.create(res.getSystemId()).getPath());
		NodeList list=doc.excuteXpathQuery("//doc", null);
		Assert.assertEquals(1, list.getLength());
		Assert.assertEquals(list.item(0).getTextContent().trim(),"cosa");
		
	}

}

class MyConverter implements Converter{
	public HashMap<String, ConverterArgument> args = new HashMap<String, ConverterArgument>();
	public URI uri;
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConverterArgument getArgument(String name) {
		// TODO Auto-generated method stub
		return args.get(name);
	}

	@Override
	public Iterable<ConverterArgument> getArguments() {
		// TODO Auto-generated method stub
		return args.values();
	}

	@Override
	public ConverterFactory getFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConverterRunnable getRunnable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URI getURI() {
		// TODO Auto-generated method stub
		return uri;
	}
	
}
