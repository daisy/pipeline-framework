//
//package org.daisy.pipeline.xproc;
//
//
//
//
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.net.URISyntaxException;
//
//import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.transform.sax.SAXSource;
//import javax.xml.transform.stream.StreamResult;
//
//import net.sf.saxon.s9api.SaxonApiException;
//
//import org.osgi.framework.BundleActivator;
//import org.osgi.framework.BundleContext;
//import org.osgi.util.tracker.ServiceTracker;
//import org.xml.sax.InputSource;
//import org.xml.sax.SAXException;
//
//
///**
// *
// * @author ndw
// */  
//public class CalabashTest implements BundleActivator{
//	private static String ROOT="/home/javi/dedicon/framework/xproc-wrapper/";
//	public static void main(String[] args) throws SaxonApiException, IOException, URISyntaxException, ParserConfigurationException, SAXException {
//		//Main main = new Main();
//		//net.sf.saxon.TransformerFactoryImpl
//		//main.run(new String[]{});
//		
//		/*
//		XProcProcessor proc = new XProcProcessor();
//		proc.getInputPorts().addPort("source", "test\\juicers.xml");
//		proc.getInputPorts().addPort("xslt", "test\\createJuicerList.xsl");
//		proc.process("test\\delete.xpl");
//	*/
//		
//		/*
//		XProcessorFactory fact = XProcessorFactory.newInstance();
//		XProcessor xproc = fact.getProcessor(new SAXSource(new InputSource(new File("test\\delete.xpl").toURI().toString())));
//		xproc.bindInputPort("source", new SAXSource(new InputSource(new File("test\\juicers.xml").toURI().toString())));
//		xproc.bindInputPort("xslt", new SAXSource(new InputSource(new File("test\\createJuicerList.xsl").toURI().toString())));
//		SAXResult saxResult = new SAXResult();
//		saxResult.setSystemId(new File("out.xml").toURI().toString());
//		xproc.bindOutputPort("result", saxResult);
//		xproc.setParameter("parameters", "name", " PACO!");
//		xproc.run();
//		*/
//		/*
//		XProcessorFactory fact = XProcessorFactory.newInstance();
//		XProcessor xproc = fact.getProcessor(new SAXSource(new InputSource(new File(ROOT+"test\\delete.xpl").toURI().toString())));
//		xproc.bindInputPort("source", new SAXSource(new InputSource(new File(ROOT+"test\\juicers.xml").toURI().toString())));
//		xproc.bindInputPort("xslt", new SAXSource(new InputSource(new File(ROOT+"test\\createJuicerList.xsl").toURI().toString())));
//		StreamResult sResult = new StreamResult(new FileOutputStream(ROOT+"out2.xml"));
//		
//		xproc.bindOutputPort("result", sResult);
//		xproc.setParameter("parameters", "name", " PACO!");
//		xproc.run();
//		*/
//		
//	}
//
//	@Override
//	public void start(BundleContext context) throws Exception {
//		final BundleContext cTxt=context;
//		System.out.println("hey!");
//		new Thread(){
//			public void run(){
//				XProcessorFactory fact=null;
//				ServiceTracker tracker;
//			
//				tracker= new ServiceTracker(cTxt, XProcessorFactory.class.getName(), null);
//				tracker.open();
//				
//				try {
//					fact = (XProcessorFactory) tracker.waitForService(5000);
//				} catch (InterruptedException e) {
//					throw new RuntimeException("Interrupted");
//				}
//				if(fact==null){
//					throw new RuntimeException("No service found");
//				}
//				tracker.close();
//				tracker=null;
//				XProcessor xproc = fact.getProcessor(new SAXSource(new InputSource(new File("test/delete.xpl").toURI().toString())));
//				xproc.bindInputPort("source", new SAXSource(new InputSource(new File("test/juicers.xml").toURI().toString())));
//				xproc.bindInputPort("xslt", new SAXSource(new InputSource(new File("test/createJuicerList.xsl").toURI().toString())));
//				StreamResult sResult;
//				try {
//					sResult = new StreamResult(new FileOutputStream("out2.xml"));
//				} catch (FileNotFoundException e) {
//					throw new RuntimeException("Error:"+e.getMessage());
//				}
//				
//				xproc.bindOutputPort("result", sResult);
//				xproc.setParameter("parameters", "name", " PACO!");
//				xproc.run();
//			}
//		}.start();
//		
//		
//		
//	}
//
//	@Override
//	public void stop(BundleContext context) throws Exception {
//		// TODO Auto-generated method stub
//		
//	}
//
//}
