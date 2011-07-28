package org.daisy.pipeline.xproc.impl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.xpath.XPathException;

import org.daisy.pipeline.xproc.InputPort;
import org.daisy.pipeline.xproc.NamedValue;
import org.daisy.pipeline.xproc.OutputPort;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XProcessorImpTest {
	XProcessorImp xProcessorImp;
	@Before
	public void setUp() throws Exception {
		LoggerFactory.getLogger(this.getClass());
	}
	 
	@Test
	public void optionTest() throws IOException, SAXException, ParserConfigurationException, IllegalArgumentException, XPathException{
		//
		URL optFile=this.getClass().getClassLoader().getResource("option.xpl");
		SAXSource src = new SAXSource();
		src.setSystemId(optFile.toString());
		xProcessorImp= (XProcessorImp) XProcFactoryImpl.newInstance().getProcessor(src);
		xProcessorImp.setOption(new NamedValue("opt", "cosa"));
		SAXResult res= new SAXResult();
		res.setSystemId(File.createTempFile("dp2", ".xml").toURI().toString());
		OutputPort port=new OutputPort("result");
		port.addBind(res);
		xProcessorImp.bindOutputPort(port);
		Assert.assertEquals("cosa",xProcessorImp.getOption("opt").getValue());
		xProcessorImp.run();
		XmlDocument doc = new XmlDocument();
		doc.setValidating(false);
		doc.loadFromFile(URI.create(res.getSystemId()).getPath());
		NodeList list=doc.excuteXpathQuery("//doc", null);
		Assert.assertEquals(1, list.getLength());
		Assert.assertEquals(list.item(0).getTextContent().trim(),"cosa");
	}
	
	@Test
	public void testInputSequence() throws IOException, SAXException, ParserConfigurationException, IllegalArgumentException, XPathException{
		//sources
		
		SAXSource src1 = new SAXSource();
		SAXSource src2 = new SAXSource();
		SAXSource xpl = new SAXSource();
		SAXResult res= new SAXResult();
		src1.setSystemId(this.getClass().getClassLoader().getResource("doc1.xml").toString());
		src2.setSystemId(this.getClass().getClassLoader().getResource("doc2.xml").toString());
		xpl.setSystemId(this.getClass().getClassLoader().getResource("inputSequence.xpl").toString());
		res.setSystemId(File.createTempFile("dp2", ".xml").toURI().toString());
		
		xProcessorImp= (XProcessorImp) XProcFactoryImpl.newInstance().getProcessor(xpl);
		//InputPort
		InputPort sourcePort= new InputPort("source");
		sourcePort.addBind(src1);
		sourcePort.addBind(src2);
		//ouptput port
		OutputPort resPort=new OutputPort("result");
		resPort.addBind(res);
		//run
		xProcessorImp.bindInputPort(sourcePort);
		xProcessorImp.bindOutputPort(resPort);
		xProcessorImp.run();
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

	@Test
	public void testOutputSequence() throws IOException, SAXException, ParserConfigurationException, IllegalArgumentException, XPathException{
		//sources
		SAXSource xpl = new SAXSource();
		SAXResult res= new SAXResult();
		
		xpl.setSystemId(this.getClass().getClassLoader().getResource("outputSequence.xpl").toString());
		res.setSystemId(File.createTempFile("dp2", ".xml").toURI().toString());
		
		xProcessorImp= (XProcessorImp) XProcFactoryImpl.newInstance().getProcessor(xpl);
		//InputPort
		
		//ouptput port
		OutputPort resPort=new OutputPort("result");
		resPort.addBind(res);
		//run
		xProcessorImp.bindOutputPort(resPort);
		xProcessorImp.run();
		//analyse the res
		XmlDocument xdoc= new XmlDocument();
		//xdoc.loadFromFile(URI.create(res.getSystemId()).getPath());
		//NodeList doc1List = xdoc.excuteXpathQuery("//doc1", null);
		//NodeList doc2List = xdoc.excuteXpathQuery("//doc2", null);
		//Assert.assertEquals(doc1List.getLength(), 1);
		//Assert.assertEquals(doc2List.getLength(), 1);
		//Assert.assertEquals("one", doc1List.item(0).getTextContent());
		//Assert.assertEquals("two", doc2List.item(0).getTextContent());
		
	}

	
}
