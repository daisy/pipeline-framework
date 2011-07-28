package org.daisy.pipeline.executor;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.xpath.XPathException;

import org.daisy.pipeline.modules.converter.Executor;
import org.daisy.pipeline.modules.converter.XProcRunnable;
import org.daisy.pipeline.xproc.InputPort;
import org.daisy.pipeline.xproc.OutputPort;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class OSGIXprocExecutorTest {
	Bundle mBundle;
	Executor mExecutor;
	@Before
	public void setUp() throws Exception {
		mBundle = FrameworkUtil.getBundle(this.getClass());
		ServiceReference serv=mBundle.getBundleContext().getServiceReference(Executor.class.getCanonicalName());
		mExecutor = (Executor)mBundle.getBundleContext().getService(serv);
	}
 
	@Test
	public void simpleExecTest() throws IOException, SAXException, ParserConfigurationException, IllegalArgumentException, XPathException, URISyntaxException{

		SAXSource src1 = new SAXSource();
		SAXSource src2 = new SAXSource();
		SAXSource xpl = new SAXSource();
		SAXResult res= new SAXResult();
		src1.setSystemId(this.getClass().getClassLoader().getResource("doc1.xml").toString());
		src2.setSystemId(this.getClass().getClassLoader().getResource("doc2.xml").toString());
		xpl.setSystemId(this.getClass().getClassLoader().getResource("inputSequence.xpl").toString());
		res.setSystemId(File.createTempFile("dp2", ".xml").toURI().toString());
		
		
		//InputPort
		InputPort sourcePort= new InputPort("source");
		sourcePort.addBind(src1);
		sourcePort.addBind(src2);
		//ouptput port
		OutputPort resPort=new OutputPort("result");
		resPort.addBind(res);
		XProcRunnable runnable = new XProcRunnable();
		runnable.setPipelineUri(this.getClass().getClassLoader().getResource("inputSequence.xpl").toURI());
		runnable.addInputPort(sourcePort);
		runnable.addOutputPort(resPort);
		mExecutor.execute(runnable);
		
		//analyse the res
		XmlDocument xdoc= new XmlDocument();
		xdoc.loadFromFile(URI.create(res.getSystemId()).getPath());
		NodeList doc1List = xdoc.excuteXpathQuery("//doc1", null);
		NodeList doc2List = xdoc.excuteXpathQuery("//doc2", null);
		Assert.assertEquals(doc1List.getLength(), 1);
		Assert.assertEquals(doc2List.getLength(), 1);
		Assert.assertEquals("one", doc1List.item(0).getTextContent());
		Assert.assertEquals("two", doc2List.item(0).getTextContent());

	}
}
