package org.daisy.pipeline.webserviceutils.xml;

import java.util.HashSet;
import java.util.List;

import org.daisy.common.messaging.Message;
import org.daisy.common.messaging.Message.Level;
import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.webserviceutils.Routes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class JobXmlWriter {
	
	Job job = null;
	List<Message> messages = null;
	boolean scriptDetails = false;
	private static Logger logger = LoggerFactory.getLogger(JobXmlWriter.class.getName());
	
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
					.createFilter().filterLevels(MSG_LEVELS)
					.getMessages();
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
		//FIXME: fix the lazy script loading
		//if (scriptDetails) {
			//ScriptXmlWriter writer = XmlWriterFactory.createXmlWriterForScript(job.getContext().getScript());
			//writer.addAsElementChild(element);
		//}
		
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

			Element resultElm = doc.createElementNS(XmlUtils.NS_PIPELINE_DATA, "result");
			String resultHref = baseUri + Routes.RESULT_ROUTE.replaceFirst("\\{id\\}", job.getId().toString());
			resultElm.setAttribute("href", resultHref);
			element.appendChild(resultElm);
		}
	}	
}
