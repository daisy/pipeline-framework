package org.daisy.pipeline.webservice;

import java.io.StringWriter;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.daisy.common.messaging.Message;
import org.daisy.common.xproc.XProcOptionInfo;
import org.daisy.common.xproc.XProcPortInfo;
import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.script.XProcOptionMetadata;
import org.daisy.pipeline.script.XProcPortMetadata;
import org.daisy.pipeline.script.XProcScript;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

public class XmlFormatter {

	private static final String NS_PIPELINE_DATA = "http://www.daisy.org/ns/pipeline/data";

	/*
	 * example output: daisy-pipeline/webservice/samples/xml-formats/job.xml
	 */
	public static Document jobToXml(Job job, String serverAddress) {
		Document doc = createDom("job");
		toXmlElm(job, doc, serverAddress);
		
		// for debugging only
		if (!Validator.validateXml(doc, Validator.jobSchema)) {
			System.out.println("INVALID XML:\n" + DOMToString(doc));
		}
		
		return doc;
	}
	
	/*
	 * example output: daisy-pipeline/webservice/samples/xml-formats/jobs.xml
	 */
	public static Document jobsToXml(Iterable<Job> jobs, String serverAddress) {
		Document doc = createDom("jobs");
		Element jobsElm = doc.getDocumentElement();
		
		Iterator<Job> it = jobs.iterator();
		while(it.hasNext()) {
			Job job = it.next();
			Element jobElm = toXmlElm(job, doc, serverAddress);
			jobsElm.appendChild(jobElm);
		}
		
		// for debugging only
		if (!Validator.validateXml(doc, Validator.jobsSchema)) {
			System.out.println("INVALID XML:\n" + DOMToString(doc));
		}
		
		return doc;
	}
	
	/*
	 * example output: daisy-pipeline/webservice/samples/xml-formats/script.xml
	 */
	public static Document xprocScriptToXml(XProcScript script) {
		Document doc = createDom("script");
		toXmlElm(script, doc);
		
		// for debugging only
		if (!Validator.validateXml(doc, Validator.scriptSchema)) {
			System.out.println("INVALID XML:\n" + DOMToString(doc));
		}
		
		return doc;
	}
	
	/*
	 * example output: daisy-pipeline/webservice/samples/xml-formats/scripts.xml
	 */
	public static Document xprocScriptsToXml(Iterable<XProcScript> scripts) {
		Document doc = createDom("scripts");
		Element scriptsElm = doc.getDocumentElement();
		
		Iterator<XProcScript> it = scripts.iterator();
		while(it.hasNext()) {
			XProcScript script = it.next();
			Element scriptElm = toXmlElm(script, doc);
			scriptsElm.appendChild(scriptElm);
		}
		
		// for debugging only
		if (!Validator.validateXml(doc, Validator.scriptsSchema)) {
			System.out.println("INVALID XML:\n" + DOMToString(doc));
		}
		
		return doc;
	}
	
	private static Element toXmlElm(XProcScript script, Document doc) {
		Element rootElm = null;
		
		if (doc.getDocumentElement().getNodeName().equals("script")) {
			rootElm = doc.getDocumentElement();
		}
		else {
			rootElm = doc.createElementNS(NS_PIPELINE_DATA, "script");
		}
		rootElm.setAttribute("href", script.getURI().toString());
		
		Element nicenameElm = doc.createElementNS(NS_PIPELINE_DATA, "nicename");
		nicenameElm.setTextContent(script.getName());
		rootElm.appendChild(nicenameElm);
		
		Element descriptionElm = doc.createElementNS(NS_PIPELINE_DATA, "description");
		descriptionElm.setTextContent(script.getDescription());
		rootElm.appendChild(descriptionElm);
		
		String homepage = script.getHomepage();
		if (homepage != null && homepage.trim().length() > 0) {
			Element homepageElm = doc.createElementNS(NS_PIPELINE_DATA, "homepage");
			homepageElm.setTextContent(homepage);
			rootElm.appendChild(homepageElm);
		}
		
		Iterator<XProcPortInfo> it_input = script.getXProcPipelineInfo().getInputPorts().iterator();
		Iterator<XProcOptionInfo> it_options = script.getXProcPipelineInfo().getOptions().iterator();
		
		while(it_input.hasNext()) {
			XProcPortInfo input = it_input.next();			
			
			Element inputElm = doc.createElementNS(NS_PIPELINE_DATA, "input");
			inputElm.setAttribute("name", input.getName());
			
			if (input.isSequence() == true) {
				inputElm.setAttribute("sequenceAllowed", "true");
			}
			else {
				inputElm.setAttribute("sequenceAllowed", "false");
			}
			
			XProcPortMetadata meta = script.getPortMetadata(input.getName());
			inputElm.setAttribute("mediaType", meta.getMediaType());
			inputElm.setAttribute("desc", meta.getDescription());

			rootElm.appendChild(inputElm);
		}
		
		while(it_options.hasNext()) {
			XProcOptionInfo option = it_options.next();
			
			Element optionElm = doc.createElementNS(NS_PIPELINE_DATA, "option");
			optionElm.setAttribute("name", option.getName().toString());
			if (option.isRequired()) {
				optionElm.setAttribute("required", "true");
			}
			else {
				optionElm.setAttribute("required", "false");
			}
			
			XProcOptionMetadata meta = script.getOptionMetadata(option.getName());
			optionElm.setAttribute("type", meta.getType());
			optionElm.setAttribute("mediaType", meta.getMediaType());
			optionElm.setAttribute("desc", meta.getDescription());
			
			rootElm.appendChild(optionElm);
		}
		
		return rootElm;
	}
	
	private static Element toXmlElm(Job job, Document doc, String serverAddress) {
		Element rootElm = null;
		
		if (doc.getDocumentElement().getNodeName().equals("job")) {
			rootElm = doc.getDocumentElement();
		}
		else {
			rootElm = doc.createElementNS(NS_PIPELINE_DATA, "job");
		}
		
		Job.Status status = job.getStatus();
		
		rootElm.setAttribute("id", job.getId().toString());
		if (status == Job.Status.DONE) {
			rootElm.setAttribute("status", "DONE");
		}
		else if (status == Job.Status.IDLE) {
			rootElm.setAttribute("status", "IDLE");
		}
		else if (status == Job.Status.RUNNING) {
			rootElm.setAttribute("status", "RUNNING");
		}
		
		Element scriptElm = doc.createElementNS(NS_PIPELINE_DATA, "script");
		scriptElm.setAttribute("href", job.getScript().getURI().toString());
		rootElm.appendChild(scriptElm);
		
		
		if (status == Job.Status.DONE) {
			Element resultElm = doc.createElementNS(NS_PIPELINE_DATA, "result");
			resultElm.setAttribute("href", serverAddress + "/jobs/" + job.getId().toString() + "/result.zip");
			rootElm.appendChild(resultElm);
		
			// list the errors and warnings
			Element messagesElm = doc.createElementNS(NS_PIPELINE_DATA, "messages");
			Iterator<Message> it_error = job.getResult().getErrors().iterator();
			while(it_error.hasNext()) {
				Element errorElm = doc.createElementNS(NS_PIPELINE_DATA, "error");
				Message err = it_error.next();
				errorElm.setTextContent(err.getMsg());
				messagesElm.appendChild(errorElm);
			}
			
			Iterator<Message> it_warn = job.getResult().getErrors().iterator();
			while(it_warn.hasNext()) {
				Element warningElm = doc.createElementNS(NS_PIPELINE_DATA, "warning");
				Message warn = it_warn.next();
				warningElm.setTextContent(warn.getMsg());
				messagesElm.appendChild(warningElm);
			}
			
			rootElm.appendChild(messagesElm);
			
			
			// reference the log file
			Element logElm = doc.createElementNS(NS_PIPELINE_DATA, "log");
			logElm.setAttribute("href", serverAddress + "/jobs/" + job.getId() + "/log");
			rootElm.appendChild(logElm);
		}
		
		return rootElm;
	}
	
	public static Document createDom(String documentElementName){
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		    DOMImplementation domImpl = documentBuilder.getDOMImplementation();
		    Document document = domImpl.createDocument(NS_PIPELINE_DATA, documentElementName, null);
		    
			return document;
		
		
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}
	    
	}	
	
	/* 
	 * from: 
	 * http://www.journaldev.com/71/utility-java-class-to-format-xml-document-to-xml-string-and-xml-to-document
	 */
	public static String DOMToString(Document doc) {
        String xmlString = "";
        if (doc != null) {
            try {
                TransformerFactory transfac = TransformerFactory.newInstance();
                Transformer trans = transfac.newTransformer();
                trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                trans.setOutputProperty(OutputKeys.INDENT, "yes");
                StringWriter sw = new StringWriter();
                StreamResult result = new StreamResult(sw);
                DOMSource source = new DOMSource(doc);
                trans.transform(source, result);
                xmlString = sw.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return xmlString;
    }
	
	public static String nodeToString(Node node) {
        Document doc = node.getOwnerDocument();
        DOMImplementationLS domImplLS = (DOMImplementationLS) doc.getImplementation();
        LSSerializer serializer = domImplLS.createLSSerializer();
        serializer.getDomConfig().setParameter("xml-declaration", false);        
        String string = serializer.writeToString(node);
        return string.trim();
    }

}
