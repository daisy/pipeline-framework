package org.daisy.pipeline.webserviceutils.xml;

import java.util.HashSet;
import java.util.List;

import javax.xml.namespace.QName;

import org.daisy.common.messaging.Message;
import org.daisy.common.messaging.Message.Level;
import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.result.JobResult;
import org.daisy.pipeline.webserviceutils.Routes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class JobXmlWriter {
	
	private Job job = null;
	private List<Message> messages = null;
	private boolean scriptDetails = false;
	private boolean fullResult=false;
	private static Logger logger = LoggerFactory.getLogger(JobXmlWriter.class
			.getName());

	private static HashSet<Level> MSG_LEVELS = new HashSet<Level>();
	static {
		MSG_LEVELS.add(Level.WARNING);
		MSG_LEVELS.add(Level.INFO);
		MSG_LEVELS.add(Level.ERROR);
	}

	public JobXmlWriter(Job job) {
		this.job = job;
	}

	public Document getXmlDocument() {
		if (job == null) {
			logger.warn("Could not create XML for null job.");
			return null;
		}
		return jobToXmlDocument();
	}

	// instead of creating a standalone XML document, add an element to an existing document
	public void addAsElementChild(Element parent) {
		Document doc = parent.getOwnerDocument();
		Element jobElm = doc.createElementNS(XmlUtils.NS_PIPELINE_DATA, "job");
		addElementData(job, jobElm);
		parent.appendChild(jobElm);
	}

	public JobXmlWriter withScriptDetails() {
		scriptDetails = true;
		return this;
	}

	public JobXmlWriter withAllMessages() {
		if (job.getContext().getMonitor().getMessageAccessor() != null) {
			messages = job.getContext().getMonitor().getMessageAccessor()
					.createFilter().filterLevels(MSG_LEVELS).getMessages();
		}
		return this;
	}

	public JobXmlWriter withMessageRange(int start, int end) {
		if (job.getContext().getMonitor().getMessageAccessor() != null) {
			messages = job.getContext().getMonitor().getMessageAccessor()
					.createFilter().filterLevels(MSG_LEVELS)
					.inRange(start, end).getMessages();
		}
		return this;
	}

	public JobXmlWriter withNewMessages(int newerThan) {
		if (job.getContext().getMonitor().getMessageAccessor() != null) {
			messages = job.getContext().getMonitor().getMessageAccessor()
					.createFilter().filterLevels(MSG_LEVELS)
					.greaterThan(newerThan).getMessages();
		}
		return this;
	}

	public JobXmlWriter withMessages(List<Message> messages) {
		this.messages = messages;
		return this;
	}

	public void withFullResults(boolean fullResult) {
		this.fullResult =fullResult;
	}
	
	private Document jobToXmlDocument() {
		Document doc = XmlUtils.createDom("job");
		Element jobElm = doc.getDocumentElement();
		addElementData(job, jobElm);
		
		// for debugging only
		if (!XmlValidator.validate(doc, XmlValidator.JOB_SCHEMA_URL)) {
			logger.error("INVALID XML:\n" + XmlUtils.DOMToString(doc));
		}

		return doc;
	}
	
	private void addElementData(Job job, Element element) {
		Document doc = element.getOwnerDocument();
		String baseUri = new Routes().getBaseUri();
		Job.Status status = job.getStatus();
		String jobHref = baseUri + Routes.JOB_ROUTE.replaceFirst("\\{id\\}", job.getId().toString());
		
		element.setAttribute("id", job.getId().toString());
		element.setAttribute("href", jobHref);
		element.setAttribute("status", status.toString());

		if(!job.getContext().getName().isEmpty()){
			Element nicenameElem= doc.createElementNS(XmlUtils.NS_PIPELINE_DATA, "nicename");
			nicenameElem.setTextContent(job.getContext().getName());
			element.appendChild(nicenameElem);
		}

		if (scriptDetails) {
			ScriptXmlWriter writer = XmlWriterFactory.createXmlWriterForScript(job.getContext().getScript());
			writer.addAsElementChild(element);
		}
		
		if (messages != null && messages.size() > 0) {
		    Element messagesElm = doc.createElementNS(XmlUtils.NS_PIPELINE_DATA, "messages");
		    element.appendChild(messagesElm);
		    
		    for (Message message : messages) {
			Element messageElm = doc.createElementNS(XmlUtils.NS_PIPELINE_DATA, "message");
			messageElm.setAttribute("level", message.getLevel().toString());
			messageElm.setAttribute("sequence", Integer.toString(message.getSequence()));
			messageElm.setTextContent(message.getText());
			messagesElm.appendChild(messageElm);
		    }
		}
		
		if (job.getStatus() == Job.Status.DONE) {
			Element logElm = doc.createElementNS(XmlUtils.NS_PIPELINE_DATA, "log");
			String logHref = baseUri + Routes.LOG_ROUTE.replaceFirst("\\{id\\}", job.getId().toString());
			logElm.setAttribute("href", logHref);
			element.appendChild(logElm);
			if(this.fullResult)
				addResults(element);
		}
	}	

	private void addResults(Element jobElem){
		Document doc = jobElem.getOwnerDocument();
		String baseUri = new Routes().getBaseUri();
		Element resultsElm = doc.createElementNS(XmlUtils.NS_PIPELINE_DATA, "results");
		String resultHref = baseUri + Routes.RESULT_ROUTE.replaceFirst("\\{id\\}", job.getId().toString());
		resultsElm.setAttribute("href", resultHref);
		resultsElm.setAttribute("mime-type", "zip");
		jobElem.appendChild(resultsElm);
		//ports
		for(String port: this.job.getContext().getResults().getPorts()){
			Element portResultElm = doc.createElementNS(XmlUtils.NS_PIPELINE_DATA, "result");
			portResultElm.setAttribute("href", String.format("%s/port/%s",resultHref,port));
			portResultElm.setAttribute("mime-type", "zip");
			portResultElm.setAttribute("from", "port");
			portResultElm.setAttribute("name", port);
			resultsElm.appendChild(portResultElm);
			for(JobResult result: this.job.getContext().getResults().getResults(port)){
				Element resultElm= doc.createElementNS(XmlUtils.NS_PIPELINE_DATA, "result");
				resultElm.setAttribute("href", String.format("%s/port/%s",resultHref,result.getIdx()));
				resultElm.setAttribute("mime-type", result.getMediaType());
				portResultElm.appendChild(resultElm);
					
			}
		}

		for(QName option: this.job.getContext().getResults().getOptions()){
			Element optionResultElm = doc.createElementNS(XmlUtils.NS_PIPELINE_DATA, "result");
			optionResultElm.setAttribute("href", String.format("%s/option/%s",resultHref,option));
			optionResultElm.setAttribute("mime-type", "zip");
			optionResultElm.setAttribute("from", "option");
			optionResultElm.setAttribute("name", option.toString());
			resultsElm.appendChild(optionResultElm);
			for(JobResult result: this.job.getContext().getResults().getResults(option)){
				Element resultElm= doc.createElementNS(XmlUtils.NS_PIPELINE_DATA, "result");
				resultElm.setAttribute("href", String.format("%s/option/%s",resultHref,result.getIdx()));
				resultElm.setAttribute("mime-type", result.getMediaType());
				optionResultElm.appendChild(resultElm);
			}
		}
	}
}
