package org.daisy.pipeline.webservice;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.daisy.common.xproc.XProcOptionInfo;
import org.daisy.common.xproc.XProcPortInfo;
import org.daisy.pipeline.script.ScriptRegistry;
import org.daisy.pipeline.script.XProcScript;
import org.daisy.pipeline.script.XProcScriptService;
import org.daisy.pipeline.webserviceutils.xml.XmlUtils;
import org.daisy.pipeline.webserviceutils.xml.XmlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

// TODO: Auto-generated Javadoc
/**
 * The Class Validator.
 */
public class Validator {

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(Validator.class.getName());

	public static boolean validateXmlAgainstSchema(Document document, URL schema) {
		return XmlValidator.validate(document, schema);
	}

	/**
	 * Validate job request.
	 *
	 * @param doc the doc
	 * @param application the application
	 * @return true, if successful
	 */
	public static boolean validateJobRequest(Document doc, PipelineWebService application) {

		// validate against the schema
		boolean xmlValid = validateXmlAgainstSchema(doc, XmlValidator.JOB_REQUEST_SCHEMA_URL);
		if (xmlValid == false) {
			return false;
		}

		boolean argsValid = validateJobRequestArguments(doc, application);
		return argsValid;
	}

	// check that there is a value for each required argument
	// check data of the argument value to the fullest extent possible
	/**
	 * Validate arguments.
	 *
	 * @param doc the doc
	 * @param application the application
	 * @return true, if successful
	 */
	private static boolean validateJobRequestArguments(Document doc, PipelineWebService application) {

		Element scriptElm = (Element)doc.getElementsByTagName("script").item(0);
		// get the ID from the href attr value
				String scriptId = scriptElm.getAttribute("href");
				if (scriptId.endsWith("/")) {
				    scriptId = scriptId.substring(0, scriptId.length() - 1);
				}
				int idx = scriptId.lastIndexOf('/');
				scriptId = scriptId.substring(idx+1);

		ScriptRegistry scriptRegistry = application.getScriptRegistry();
		XProcScriptService unfilteredScript = scriptRegistry.getScript(scriptId);

		if (unfilteredScript == null) {
			logger.error("Script not found");
			return false;
		}

		XProcScript script;
		if (application.isLocal()) {
			script = unfilteredScript.load();
		}
		else {
			script = XProcScriptFilter.INSTANCE.filter(unfilteredScript.load());
		}

		// inputs
		boolean hasAllRequiredInputs = validateJobRequestInputPortData(script.getXProcPipelineInfo().getInputPorts(),
				doc.getElementsByTagName("input"), script);
		// options
		boolean hasAllRequiredOptions = validateJobRequestOptionData(script.getXProcPipelineInfo().getOptions(),
				doc.getElementsByTagName("option"), script);
		// outputs (if run in local mode)
		boolean hasAllRequiredOutputs = validateJobRequestOutputPortData(script
				.getXProcPipelineInfo().getOutputPorts(),
				doc.getElementsByTagName("output"), script);

		if (application.isLocal()) {
			return hasAllRequiredInputs & hasAllRequiredOutputs & hasAllRequiredOptions;
		}
		else {
			return hasAllRequiredInputs & hasAllRequiredOptions;
		}
	}

	/**
	 * Validate option data.
	 *
	 * @param options the options
	 * @param nodes the nodes
	 * @param script the script
	 * @return true, if successful
	 */
	private static boolean validateJobRequestOptionData(Iterable<XProcOptionInfo> options, NodeList nodes, XProcScript script) {
		Iterator<XProcOptionInfo>it = options.iterator();
		boolean hasAllRequiredArgs = true;

		while (it.hasNext()) {
			XProcOptionInfo arg = it.next();
			// skip optional arguments
			if (arg.isRequired() == false) {
				continue;
			}

			boolean validArg = false;
			for (int i=0; i<nodes.getLength(); i++) {
				Element elm = (Element)nodes.item(i);
				if (elm.getAttribute("name").equals(arg.getName().toString())) {
					validArg = validateOptionType(elm.getTextContent(), script.getOptionMetadata(arg.getName()).getMediaType());
					break;
				}
			}
			hasAllRequiredArgs &= validArg;
		}

		if (hasAllRequiredArgs == false) {
			// TODO: be more specific
			logger.error("Required args missing");
		}
		return hasAllRequiredArgs;
	}

	/**
	 * Validate input port data.
	 *
	 * @param ports the ports
	 * @param nodes the nodes
	 * @param script the script
	 * @return true, if successful
	 */
	private static boolean validateJobRequestInputPortData(Iterable<XProcPortInfo> ports, NodeList nodes, XProcScript script) {

		Iterator<XProcPortInfo>it = ports.iterator();
		boolean hasAllRequiredArgs = true;
		while (it.hasNext()) {
			XProcPortInfo arg = it.next();
			boolean validArg = false;
			// input elements should be of one of two forms:
			// <input name="in1">
			//   <item value="./path/to/file/book.xml/>
			//   ...
			// </input>
			//
			// OR
			//
			// <input name="in">
			//   <docwrapper>
			//     <xml data../>
			//  </docwrapper>
			// </input>
			//
			//
			for (int i=0; i<nodes.getLength(); i++) {
				Element elm = (Element)nodes.item(i);

				// find the <input> XML element that matches this input arg name
				if (elm.getAttribute("name").equals(arg.getName())) {
					// <input> elements will have either <item> element children or <docwrapper> element children
					NodeList itemNodes = elm.getElementsByTagName("item");
					NodeList docwrapperNodes = elm.getElementsByTagName("docwrapper");

					if (itemNodes.getLength() == 0 && docwrapperNodes.getLength() == 0) {
						validArg = false;
					}
					else {
						if (itemNodes.getLength() > 0) {
							validArg = validateItemElements(itemNodes);
						}
						else {
							validArg = validateDocwrapperElements(docwrapperNodes, script.getPortMetadata(arg.getName()).getMediaType());
						}
					}
					break;
				}
			}
			hasAllRequiredArgs &= validArg;
		}

		if (hasAllRequiredArgs == false) {
			// TODO: be more specific
			logger.error("Required args missing");
		}
		return hasAllRequiredArgs;
	}

	/**
	 * Validate output port data.
	 *
	 * @param ports the ports
	 * @param nodes the nodes
	 * @param script the script
	 * @return true, if successful
	 */
	private static boolean validateJobRequestOutputPortData(Iterable<XProcPortInfo> ports, NodeList nodes, XProcScript script) {
		Iterator<XProcPortInfo>it = ports.iterator();
		boolean hasAllRequiredArgs = true;

		while (it.hasNext()) {
			XProcPortInfo arg = it.next();
			boolean validArg = false;
			for (int i=0; i<nodes.getLength(); i++) {
				Element elm = (Element)nodes.item(i);
				if (elm.getAttribute("name").equals(arg.getName())) {
					NodeList itemNodes = elm.getElementsByTagName("item");

					if (itemNodes.getLength() == 0) {
						validArg = false;
					}
					else {
						if (itemNodes.getLength() > 0) {
							validArg = validateItemElements(itemNodes);
						}
					}
					break;
				}
			}
			hasAllRequiredArgs &= validArg;
		}

		if (hasAllRequiredArgs == false) {
			// TODO: be more specific
			System.out.println("ERROR: Required args missing");
		}
		return hasAllRequiredArgs;
	}

	// make sure these nodes contain well-formed XML
	// nodes must contain at least one item
	// nodes must be <docwrapper> elements
	// TODO incorporate media type
	/**
	 * Validate docwrapper elements.
	 *
	 * @param nodes the nodes
	 * @param mediaType the media type
	 * @return true, if successful
	 */
	private static boolean validateDocwrapperElements(NodeList nodes, String mediaType) {
		boolean isValid = true;

		for (int i = 0; i<nodes.getLength(); i++) {
			Node docwrapper = nodes.item(i);
			Node content = null;
			// find the first element child of docwrapper
			for (int q = 0; q < docwrapper.getChildNodes().getLength(); q++) {
				if (docwrapper.getChildNodes().item(q).getNodeType() == Node.ELEMENT_NODE) {
					content = docwrapper.getChildNodes().item(q);
					break;
				}
			}
			String xml = XmlUtils.nodeToString(content);
			isValid &= validateWellFormedXml(xml);
		}

		return isValid;
	}

	// make sure these @value attributes are non-empty
	// nodes must contain at least one item
	// all nodes must be <item> elements
	/**
	 * Validate item elements.
	 *
	 * @param nodes the nodes
	 * @return true, if successful
	 */
	private static boolean validateItemElements(NodeList nodes) {
		boolean isValid = true;

		for (int i = 0; i<nodes.getLength(); i++) {
			Element elm = (Element)nodes.item(i);
			isValid &= elm.getAttribute("value").trim().length() > 0;
		}
		return isValid;
	}

	/**
	 * Validate option type.
	 *
	 * @param value the value
	 * @param mediaType the media type
	 * @return true, if successful
	 */
	private static boolean validateOptionType(String value, String mediaType) {
		// TODO validate XSD types
		// for now, just check that the string is non-empty
		return value.trim().length() > 0;
	}

	// just validate whether the xml is well-formed or not.
	// we don't verify flavor of xml is expected;
	// that's expected to be handled by the xproc script itself
	/**
	 * Validate well formed xml.
	 *
	 * @param xml the xml
	 * @return true, if successful
	 */
	private static boolean validateWellFormedXml(String xml){

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    factory.setValidating(false);
	    DocumentBuilder db;
		try {
			db = factory.newDocumentBuilder();
			InputStream is = new ByteArrayInputStream(xml.getBytes());
		    db.parse(is);
		    is.close();
		} catch (ParserConfigurationException e) {
			logger.error(e.getMessage());
			return false;
		} catch (SAXException e) {
			logger.error(e.getMessage());
			return false;
		} catch (IOException e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}

}
