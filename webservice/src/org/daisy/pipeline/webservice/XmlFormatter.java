package org.daisy.pipeline.webservice;

import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.daisy.pipeline.jobmanager.Job;
import org.daisy.pipeline.jobmanager.JobStatus;
import org.daisy.pipeline.modules.converter.Converter.ConverterArgument;
import org.daisy.pipeline.modules.converter.ConverterDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

public class XmlFormatter {

	public static Document jobToXml(Job job, String serverAddress) {
		Document doc = createDom();
		Element jobElm = toXmlElm(job, doc, serverAddress);
		doc.appendChild(jobElm);
		return doc;
	}
	
	public static Document jobsToXml(Iterable<Job> jobs, String serverAddress) {
		Document doc = createDom();
		Element jobsElm = doc.createElement("jobs");
		
		Iterator<Job> it = jobs.iterator();
		while(it.hasNext()) {
			Job job = it.next();
			Element jobElm = toXmlElm(job, doc, serverAddress);
			jobsElm.appendChild(jobElm);
		}
		
		doc.appendChild(jobsElm);
		return doc;
	}
	

	public static Document converterDescriptorToXml(ConverterDescriptor converterDescriptor) {
		Document doc = createDom();
		Element converterElm = toXmlElm(converterDescriptor, doc);
		doc.appendChild(converterElm);
		return doc;
	}
	
	public static Document converterDescriptorsToXml(Iterable<ConverterDescriptor> converterDescriptors) {
		Document doc = createDom();
		Element convertersElm = doc.createElement("converters");
		
		Iterator<ConverterDescriptor> it = converterDescriptors.iterator();
		while(it.hasNext()) {
			ConverterDescriptor converterDescriptor = it.next();
			Element converterElm = toXmlElm(converterDescriptor, doc);
			convertersElm.appendChild(converterElm);
		}
		
		doc.appendChild(convertersElm);
		return doc;
	}
	
	/*
	<converter href="http://www.daisy.org/ns/pipeline/modules/dtbook-to-zedai/dtbook-to-zedai.xpl">
	    <description>Convert DTBook XML to ZedAI XML</description>  
	    <arg  name="in"  type="input" port="source" desc="input document"/>  
		<!-- only need to list input arguments -->      
		<!-- more arguments could follow, depending on the converter -->
		...
	</converter>
	 */
	private static Element toXmlElm(ConverterDescriptor converterDescriptor, Document doc) {
		Element rootElm = doc.createElement("converter");
		rootElm.setAttribute("href", converterDescriptor.getFile().toString());
		
		Element descriptionElm = doc.createElement("description");
		descriptionElm.setTextContent(converterDescriptor.getDescription());
		
		rootElm.appendChild(descriptionElm);
		
		Iterator<ConverterArgument> it = converterDescriptor.getConverter().getArguments().iterator();
		
		while(it.hasNext()) {
			ConverterArgument arg = it.next();
			
			String type = "";
			if (arg.getType() == ConverterArgument.Type.INPUT) {
				type = "input";
			}
			else if (arg.getType() == ConverterArgument.Type.OPTION) {
				type = "option";
			}
			else if (arg.getType() == ConverterArgument.Type.OUTPUT) {
				type = "output";
			}
			else if (arg.getType() == ConverterArgument.Type.PARAMETER) {
				type = "parameter";
			}
			Element argElm = doc.createElement("arg");
			argElm.setAttribute("name", arg.getName());
			argElm.setAttribute("type", type);
			argElm.setAttribute("port", arg.getPort());
			argElm.setAttribute("desc", arg.getDesc());
			
			rootElm.appendChild(argElm);
		}
		return rootElm;
	}
	
	/*
	<job id="job-id" status="PROCESSING | COMPLETED | FAILED | NOT_STARTED">
	  <converter href="http://www.daisy.org/ns/pipeline/modules/dtbook-to-zedai/dtbook-to-zedai.xpl"/>
	  <result href="http://ws.pipeline.org/jobs/$ID/result"/>
	  <errors>
	     <error level="WARNING | FATAL | ERROR">This is a description of the error</error>
	     ...
	  </errors>
	  <log href="http://ws.pipeline.org/jobs/$ID/log"/>
	</job>
	 */
	private static Element toXmlElm(Job job, Document doc, String serverAddress) {
		JobStatus jobStatus = job.getStatus();
		
		Element rootElm = doc.createElement("job");
		rootElm.setAttribute("id", job.getId().getID());
		if (jobStatus.getStatus() == JobStatus.Status.COMPLETED) {
			rootElm.setAttribute("status", "COMPLETED");
		}
		else if (jobStatus.getStatus() == JobStatus.Status.PROCESSING) {
			rootElm.setAttribute("status", "PROCESSING");
		}
		else if (jobStatus.getStatus() == JobStatus.Status.FAILED) {
			rootElm.setAttribute("status", "FAILED");
		}
		else if (jobStatus.getStatus() == JobStatus.Status.NOT_STARTED) {
			rootElm.setAttribute("status", "NOT_STARTED");
		}

		// TODO: is the converter element really necessary? it's not available via the job object.  it also doesn't provide any new info to the client.
		Element converterElm = doc.createElement("converter");
		converterElm.setAttribute("href", "NA");
		rootElm.appendChild(converterElm);
		
		
		if (jobStatus.getStatus() == JobStatus.Status.COMPLETED) {
			Element resultElm = doc.createElement("result");
			resultElm.setAttribute("href", serverAddress + "/jobs/" + job.getId() + "/result");
			rootElm.appendChild(resultElm);
		}
		
		Element errorsElm = doc.createElement("errors");
		// TODO: is this the best way to get the error objects?
		Iterator<org.daisy.pipeline.jobmanager.Error> it = jobStatus.getErrors().iterator();
		
		while(it.hasNext()) {
			Element errorElm = doc.createElement("error");
			JobStatus.JobError err = (JobStatus.JobError)it.next();
			if (err.getLevel() == org.daisy.pipeline.jobmanager.Error.Level.WARNING) {
				errorElm.setAttribute("level", "warning");
			}
			else if (err.getLevel() == org.daisy.pipeline.jobmanager.Error.Level.FATAL) {
				errorElm.setAttribute("level", "fatal");
			} 
			else if (err.getLevel() == org.daisy.pipeline.jobmanager.Error.Level.ERROR) {
				errorElm.setAttribute("level", "error");
			}
			errorElm.setTextContent(err.getDescription());
			errorsElm.appendChild(errorElm);
		}
		
		rootElm.appendChild(errorsElm);
		
		if (jobStatus.getStatus() == JobStatus.Status.COMPLETED || jobStatus.getStatus() == JobStatus.Status.FAILED) {
			Element logElm = doc.createElement("log");
			logElm.setAttribute("href", serverAddress + "/jobs/" + job.getId() + "/log");
		}
		return rootElm;
	}
	
	public static Document createDom(){
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		    Document document = documentBuilder.newDocument();
			//DomRepresentation rep = new DomRepresentation();
			//Document document = rep.getDocument();
		    return document;
		
		
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	    
	}
	
	// TODO: this doesn't quite work as I would like .. it includes the parent node
	public static String nodeToString(Node node) {
		Document doc = node.getOwnerDocument();
		DOMImplementationLS domImplLS = (DOMImplementationLS) doc.getImplementation();
		LSSerializer serializer = domImplLS.createLSSerializer();
		return serializer.writeToString(node);
	}
}
