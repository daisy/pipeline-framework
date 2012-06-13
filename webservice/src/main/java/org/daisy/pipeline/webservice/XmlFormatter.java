package org.daisy.pipeline.webservice;

import java.io.StringWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.daisy.common.messaging.Message;
import org.daisy.common.messaging.Message.Level;
import org.daisy.common.xproc.XProcOptionInfo;
import org.daisy.common.xproc.XProcPortInfo;
import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.script.XProcOptionMetadata;
import org.daisy.pipeline.script.XProcPortMetadata;
import org.daisy.pipeline.script.XProcScript;
import org.daisy.pipeline.webservice.clients.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

// TODO: Auto-generated Javadoc
/**
 * The Class XmlFormatter.
 */
public class XmlFormatter {

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(XmlFormatter.class
			.getName());

	private static HashSet<Level> MSG_LEVELS = new HashSet<Level>();
	static {
		MSG_LEVELS.add(Level.WARNING);
		MSG_LEVELS.add(Level.INFO);
		MSG_LEVELS.add(Level.ERROR);
	}

	/*
	 * example output: daisy-pipeline/webservice/docs/sampleXml/job.xml
	 */
	/**
	 * Job to xml.
	 *
	 * @param job
	 *            the job
	 * @param serverAddress
	 *            the server address
	 * @return the document
	 */
	public static Document jobToXml(Job job, int msgSeq, String baseUri) {
		Document doc = XmlFormatter.createDom("job");
		toXmlElm(job, doc, msgSeq, baseUri, true);

		// for debugging only
		if (!Validator.validateXmlAgainstSchema(doc, Validator.JOB_SCHEMA_URL)) {
			logger.error("INVALID XML:\n" + XmlFormatter.DOMToString(doc));
		}

		return doc;
	}

	/*
	 * example output: daisy-pipeline/webservice/docs/sampleXml/jobs.xml
	 */
	/**
	 * Jobs to xml.
	 *
	 * @param jobs
	 *            the jobs
	 * @param serverAddress
	 *            the server address
	 * @return the document
	 */
	public static Document jobsToXml(Iterable<Job> jobs, String baseUri) {
		Document doc = XmlFormatter.createDom("jobs");
		Element jobsElm = doc.getDocumentElement();
		jobsElm.setAttribute("href", baseUri + PipelineWebService.JOBS_ROUTE);

		Iterator<Job> it = jobs.iterator();
		while (it.hasNext()) {
			Job job = it.next();
			Element jobElm = toXmlElm(job, doc, 0, baseUri, false);
			jobsElm.appendChild(jobElm);
		}

		// for debugging only
		if (!Validator.validateXmlAgainstSchema(doc, Validator.JOBS_SCHEMA_URL)) {
			logger.error("INVALID XML:\n" + XmlFormatter.DOMToString(doc));
		}

		return doc;
	}

	/*
	 * example output: daisy-pipeline/webservice/docs/sampleXml/script.xml
	 */
	/**
	 * Xproc script to xml.
	 *
	 * @param script
	 *            the script
	 * @return the document
	 */
	public static Document xprocScriptToXml(XProcScript script, String baseUri) {
		Document doc = XmlFormatter.createDom("script");
		toXmlElm(script, doc, baseUri, true);

		// for debugging only
		if (!Validator.validateXmlAgainstSchema(doc, Validator.SCRIPT_SCHEMA_URL)) {
			logger.error("INVALID XML:\n" + XmlFormatter.DOMToString(doc));
		}

		return doc;
	}

	/*
	 * example output: daisy-pipeline/webservice/docs/sampleXml/scripts.xml
	 */
	/**
	 * Xproc scripts to xml.
	 *
	 * @param scripts
	 *            the scripts
	 * @return the document
	 */
	public static Document xprocScriptsToXml(Iterable<XProcScript> scripts, String baseUri) {
		Document doc = XmlFormatter.createDom("scripts");
		Element scriptsElm = doc.getDocumentElement();
		scriptsElm.setAttribute("href", baseUri + PipelineWebService.SCRIPTS_ROUTE);
		Iterator<XProcScript> it = scripts.iterator();
		while (it.hasNext()) {
			XProcScript script = it.next();
			Element scriptElm = toXmlElm(script, doc, baseUri, false);
			scriptsElm.appendChild(scriptElm);
		}

		// for debugging only
		if (!Validator.validateXmlAgainstSchema(doc, Validator.SCRIPTS_SCHEMA_URL)) {
			logger.error("INVALID XML:\n" + XmlFormatter.DOMToString(doc));
		}

		return doc;
	}

	public static Document clientToXml(Client client, String baseUri) {
		Document doc = XmlFormatter.createDom("client");
		XmlFormatter.toXmlElm(client, doc, baseUri);

		// for debugging only
		if (!Validator.validateXmlAgainstSchema(doc, Validator.CLIENT_SCHEMA_URL)) {
			XmlFormatter.logger.error("INVALID XML:\n" + XmlFormatter.DOMToString(doc));
		}

		return doc;
	}

	public static Document clientsToXml(List<? extends Client> clients, String baseUri) {
		Document doc = XmlFormatter.createDom("clients");
		Element clientsElm = doc.getDocumentElement();
		clientsElm.setAttribute("href", baseUri + PipelineWebService.CLIENTS_ROUTE);
		for (Client client : clients) {
			Element clientElm = XmlFormatter.toXmlElm(client, doc, baseUri);
			clientsElm.appendChild(clientElm);
		}
		// for debugging only
		if (!Validator.validateXmlAgainstSchema(doc, Validator.CLIENTS_SCHEMA_URL)) {
			XmlFormatter.logger.error("INVALID XML:\n" + XmlFormatter.DOMToString(doc));
		}

		return doc;
	}


	private static Element toXmlElm(XProcScript script, Document doc, String baseUri, boolean detail) {
		Element rootElm = null;

		if (doc.getDocumentElement().getNodeName().equals("script")) {
			rootElm = doc.getDocumentElement();
		} else {
			rootElm = doc.createElementNS(XmlFormatter.NS_PIPELINE_DATA,
					"script");
		}
		logger.debug("Script: "+script.getName());
		logger.debug("Descriptor: "+script.getDescriptor());
		String scriptHref = baseUri + PipelineWebService.SCRIPT_ROUTE.replaceFirst("\\{id\\}", script.getDescriptor().getId());

		rootElm.setAttribute("id", script.getDescriptor().getId());
		rootElm.setAttribute("href", scriptHref);

		Element nicenameElm = doc.createElementNS(
				XmlFormatter.NS_PIPELINE_DATA, "nicename");
		nicenameElm.setTextContent(script.getName());
		rootElm.appendChild(nicenameElm);

		Element descriptionElm = doc.createElementNS(
				XmlFormatter.NS_PIPELINE_DATA, "description");
		descriptionElm.setTextContent(script.getDescription());
		rootElm.appendChild(descriptionElm);

		if (detail) {
			String homepage = script.getHomepage();
			if (homepage != null && homepage.trim().length() > 0) {
				Element homepageElm = doc.createElementNS(
						XmlFormatter.NS_PIPELINE_DATA, "homepage");
				homepageElm.setTextContent(homepage);
				rootElm.appendChild(homepageElm);
			}

			Iterator<XProcPortInfo> it_input = script.getXProcPipelineInfo()
					.getInputPorts().iterator();
			Iterator<XProcOptionInfo> it_options = script
					.getXProcPipelineInfo().getOptions().iterator();
			Iterator<XProcPortInfo> it_output = script.getXProcPipelineInfo()
					.getOutputPorts().iterator();

			while (it_input.hasNext()) {
				XProcPortInfo input = it_input.next();

				Element inputElm = doc.createElementNS(
						XmlFormatter.NS_PIPELINE_DATA, "input");
				inputElm.setAttribute("name", input.getName());

				if (input.isSequence() == true) {
					inputElm.setAttribute("sequence", "true");
				} else {
					inputElm.setAttribute("sequence", "false");
				}


				XProcPortMetadata meta = script
						.getPortMetadata(input.getName());
				inputElm.setAttribute("mediaType", meta.getMediaType());
				inputElm.setAttribute("desc", meta.getDescription());

				rootElm.appendChild(inputElm);
			}

			while (it_options.hasNext()) {
				XProcOptionInfo option = it_options.next();

				Element optionElm = doc.createElementNS(XmlFormatter.NS_PIPELINE_DATA, "option");
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


				if (meta.isOrdered()) {
					optionElm.setAttribute("ordered", "true");
				}
				else {
					optionElm.setAttribute("ordered", "false");
				}

				if (meta.isSequence()) {
					optionElm.setAttribute("sequence", "true");
				}
				else {
					optionElm.setAttribute("sequence", "false");
				}

				if (meta.isOrdered()) {
					optionElm.setAttribute("ordered", "true");
				}
				else {
					optionElm.setAttribute("ordered", "false");
				}

				rootElm.appendChild(optionElm);
			}

			while (it_output.hasNext()) {
				XProcPortInfo output = it_output.next();

				Element outputElm = doc.createElementNS(
						XmlFormatter.NS_PIPELINE_DATA, "output");
				outputElm.setAttribute("name", output.getName());

				if (output.isSequence() == true) {
					outputElm.setAttribute("sequence", "true");
				}
				else {
					outputElm.setAttribute("sequence", "false");
				}
				XProcPortMetadata meta = script.getPortMetadata(output
						.getName());
				outputElm.setAttribute("mediaType", meta.getMediaType());
				outputElm.setAttribute("desc", meta.getDescription());

				rootElm.appendChild(outputElm);
			}
		}

		return rootElm;
	}

	/**
	 * To xml elm.
	 *
	 * @param job
	 *            the job
	 * @param doc
	 *            the doc
	 * @param serverAddress
	 *            the server address
	 * @return the element
	 */
	private static Element toXmlElm(Job job, Document doc, int msgSeq, String baseUri, boolean detail) {
		Element rootElm = null;

		if (doc.getDocumentElement().getNodeName().equals("job")) {
			rootElm = doc.getDocumentElement();
		} else {
			rootElm = doc.createElementNS(XmlFormatter.NS_PIPELINE_DATA, "job");
		}

		Job.Status status = job.getStatus();
		String jobHref = baseUri + PipelineWebService.JOB_ROUTE.replaceFirst("\\{id\\}", job.getId().toString());

		rootElm.setAttribute("id", job.getId().toString());
		rootElm.setAttribute("href", jobHref);
		rootElm.setAttribute("status", status.toString());

		if (detail) {
			Element scriptElm = toXmlElm(job.getScript(), doc, baseUri, false);
            rootElm.appendChild(scriptElm);

            Element messagesElm = doc.createElementNS(
                            XmlFormatter.NS_PIPELINE_DATA, "messages");

            if (job.getMonitor().getMessageAccessor() != null) {
                List<Message> msgs = job.getMonitor().getMessageAccessor()
                                .createFilter().filterLevels(MSG_LEVELS)
                                .fromSquence(msgSeq).getMessages();

                for (Message msg : msgs) {
                        Element singleMsgElm = doc.createElementNS(
                                        XmlFormatter.NS_PIPELINE_DATA, "message");
                        singleMsgElm.setAttribute("level", msg.getLevel()
                                        .toString());
                        singleMsgElm.setAttribute("sequence", msg.getSequence()
                                        + "");
                        singleMsgElm.setTextContent(msg.getText());
                        messagesElm.appendChild(singleMsgElm);
                }
                if (msgs.size() > 0) {
                        rootElm.appendChild(messagesElm);
                }
        }

			if (job.getStatus() == Job.Status.DONE) {
				Element logElm = doc.createElementNS(XmlFormatter.NS_PIPELINE_DATA, "log");
				String logHref = baseUri + PipelineWebService.LOG_ROUTE.replaceFirst("\\{id\\}", job.getId().toString());
				logElm.setAttribute("href", logHref);
				rootElm.appendChild(logElm);

				Element resultElm = doc.createElementNS(XmlFormatter.NS_PIPELINE_DATA, "result");
				String resultHref = baseUri + PipelineWebService.RESULT_ROUTE.replaceFirst("\\{id\\}", job.getId().toString());
				resultElm.setAttribute("href", resultHref);
				rootElm.appendChild(resultElm);
			}
		}
		return rootElm;
	}

	private static Element toXmlElm(Client client, Document doc, String baseUri) {
		Element rootElm = null;

		if (doc.getDocumentElement().getNodeName().equals("client")) {
			rootElm = doc.getDocumentElement();
		} else {
			rootElm = doc.createElementNS(XmlFormatter.NS_PIPELINE_DATA,
					"client");
		}

		String clientHref = baseUri + PipelineWebService.CLIENT_ROUTE.replaceFirst("\\{id\\}", client.getId());

		rootElm.setAttribute("id", client.getId());
		rootElm.setAttribute("href", clientHref);
		rootElm.setAttribute("secret", client.getSecret());
		rootElm.setAttribute("role", client.getRole().toString());
		rootElm.setAttribute("contact", client.getContactInfo());

		return rootElm;
	}

	/*
	 * from:
	 * http://www.journaldev.com/71/utility-java-class-to-format-xml-document
	 * -to-xml-string-and-xml-to-document
	 */
	/**
	 * DOM to string.
	 *
	 * @param doc
	 *            the doc
	 * @return the string
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

	/** The Constant NS_PIPELINE_DATA. */
	public static final String NS_PIPELINE_DATA = "http://www.daisy.org/ns/pipeline/data";

	/**
	 * Node to string.
	 *
	 * @param node
	 *            the node
	 * @return the string
	 */
	public static String nodeToString(Node node) {
		Document doc = node.getOwnerDocument();
		DOMImplementationLS domImplLS = (DOMImplementationLS) doc
				.getImplementation();
		LSSerializer serializer = domImplLS.createLSSerializer();
		serializer.getDomConfig().setParameter("xml-declaration", false);
		String string = serializer.writeToString(node);
		return string.trim();
	}

	/**
	 * Creates the dom.
	 *
	 * @param documentElementName
	 *            the document element name
	 * @return the document
	 */
	public static Document createDom(String documentElementName) {
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			DOMImplementation domImpl = documentBuilder.getDOMImplementation();
			Document document = domImpl.createDocument(NS_PIPELINE_DATA,
					documentElementName, null);

			return document;

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}

	}

}
