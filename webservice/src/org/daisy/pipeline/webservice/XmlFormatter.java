package org.daisy.pipeline.webservice;

import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.daisy.common.xproc.XProcOptionInfo;
import org.daisy.common.xproc.XProcPortInfo;
import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.script.XProcOptionMetadata;
import org.daisy.pipeline.script.XProcPortMetadata;
import org.daisy.pipeline.script.XProcScript;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlFormatter {

	/*
	 * example output: daisy-pipeline/webservice/docs/sampleXml/job.xml
	 */
	public static Document jobToXml(Job job, String serverAddress) {
		Document doc = createDom("job");
		toXmlElm(job, doc, serverAddress);
		return doc;
	}
	
	/*
	 * example output: daisy-pipeline/webservice/docs/sampleXml/jobs.xml
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
		
		return doc;
	}
	
	/*
	 * example output: daisy-pipeline/webservice/docs/sampleXml/script.xml
	 */
	public static Document xprocScriptToXml(XProcScript script) {
		Document doc = createDom("script");
		toXmlElm(script, doc);
		return doc;
	}
	
	/*
	 * example output: daisy-pipeline/webservice/docs/sampleXml/scripts.xml
	 */
	public static Document xprocScriptsToXml(Iterable<XProcScript> scripts) {
		Document doc = createDom("scripts");
		Element scriptsElm = doc.getDocumentElement();
		
		Iterator<XProcScript> it = scripts.iterator();
		while(it.hasNext()) {
			XProcScript script = it.next();
			Element converterElm = toXmlElm(script, doc);
			scriptsElm.appendChild(converterElm);
		}
		return doc;
	}
	
	private static Element toXmlElm(XProcScript script, Document doc) {
		Element rootElm = null;
		
		if (doc.getDocumentElement().getNodeName() == "script") {
			rootElm = doc.getDocumentElement();
		}
		else {
			rootElm = doc.createElement("script");
		}
		rootElm.setAttribute("href", script.getURI().toString());
		
		Element descriptionElm = doc.createElement("description");
		descriptionElm.setTextContent(script.getDescription());
		
		rootElm.appendChild(descriptionElm);
		
		Iterator<XProcPortInfo> it_input = script.getXProcPipelineInfo().getInputPorts().iterator();
		Iterator<XProcOptionInfo> it_options = script.getXProcPipelineInfo().getOptions().iterator();
		
		while(it_input.hasNext()) {
			XProcPortInfo input = it_input.next();			
			
			Element inputElm = doc.createElement("input");
			inputElm.setAttribute("name", input.getName());
			
			if (input.isSequence() == true) {
				inputElm.setAttribute("sequenceAllowed", "true");
			}
			else {
				inputElm.setAttribute("sequenceAllowed", "false");
			}
			
			XProcPortMetadata meta = script.getPortMetadata(input.getName());
			inputElm.setAttribute("type", meta.getMediaType());
			inputElm.setAttribute("desc", meta.getDescription());

			rootElm.appendChild(inputElm);
		}
		
		while(it_options.hasNext()) {
			XProcOptionInfo option = it_options.next();
			
			Element optionElm = doc.createElement("option");
			optionElm.setAttribute("name", option.getName().toString());
			if (option.isRequired()) {
				optionElm.setAttribute("required", "true");
			}
			else {
				optionElm.setAttribute("required", "false");
			}
			
			XProcOptionMetadata meta = script.getOptionMetadata(option.getName());
			optionElm.setAttribute("type", meta.getMediaType());
			optionElm.setAttribute("desc", meta.getDescription());
		}
		
		return rootElm;
	}
	
	private static Element toXmlElm(Job job, Document doc, String serverAddress) {
		Element rootElm = null;
		
		if (doc.getDocumentElement().getNodeName() == "job") {
			rootElm = doc.getDocumentElement();
		}
		else {
			rootElm = doc.createElement("job");
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
		
		// TODO: get the script URI from the job (pending framework implementation)
		Element scriptElm = doc.createElement("script");
		scriptElm.setAttribute("href", "NA");
		rootElm.appendChild(scriptElm);
		
		
		if (status == Job.Status.DONE) {
			Element resultElm = doc.createElement("result");
			resultElm.setAttribute("href", serverAddress + "/jobs/" + job.getId().toString() + "/result");
			rootElm.appendChild(resultElm);
		}
		
		/*
		 * TODO incorporate errors (pending framework implementation)
		 
		Element errorsElm = doc.createElement("errors");
		
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
		
		rootElm.appendChild(errorsElm);*/
		
		if (status == Job.Status.DONE) {
			Element logElm = doc.createElement("log");
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
		    Document document = domImpl.createDocument("http://www.daisy.org/ns/pipeline/data", documentElementName, null);
			return document;
		
		
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}
	    
	}	
}
