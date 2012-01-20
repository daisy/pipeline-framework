package org.daisy.pipeline.webservice;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.daisy.common.xproc.XProcOptionInfo;
import org.daisy.common.xproc.XProcPortInfo;
import org.daisy.pipeline.script.ScriptRegistry;
import org.daisy.pipeline.script.XProcScript;
import org.daisy.pipeline.script.XProcScriptService;
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
	
	// although in everyday practice, the function validateJobRequest will be the most-used, 
	// all the schema URLs are included here so that during testing, the web service can validate
	// its own output by calling validateXml with the appropriate schema URL.
	/** The Constant scriptSchema. */
	public static final URL scriptSchema = Validator.class.getResource("resources/script.xsd");
	
	/** The Constant scriptsSchema. */
	public static final URL scriptsSchema = Validator.class.getResource("resources/scripts.xsd");
	
	/** The Constant jobSchema. */
	public static final URL jobSchema = Validator.class.getResource("resources/job.xsd");
	
	/** The Constant jobRequestSchema. */
	public static final URL jobRequestSchema = Validator.class.getResource("resources/jobRequest.xsd");
	
	/** The Constant jobsSchema. */
	public static final URL jobsSchema = Validator.class.getResource("resources/jobs.xsd");
	
	/** The Constant logSchema. */
	public static final URL logSchema = Validator.class.getResource("resources/log.xsd");
	
	public static final URL clientSchema = Validator.class.getResource("resources/client.xsd");
	
	public static final URL clientsSchema = Validator.class.getResource("resources/clients.xsd");
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(Validator.class.getName());
	
	// If the Document isn't namespace-aware, this will likely fail
	/**
	 * Validate xml.
	 *
	 * @param document the document
	 * @param schemaUrl the schema url
	 * @return true, if successful
	 */
	public static boolean validateXml(Document document, URL schemaUrl) {
	    
		if (document == null) {
			logger.error("Could not validate null document");
			return false;
		}
		
		if (schemaUrl == null) {
			logger.error("Could not validate -- no schema given.");
			return false;
		}
		
		XmlResourceResolver resolver = new XmlResourceResolver();
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		factory.setResourceResolver(resolver);
	    Source schemaFile;
	    InputStream is = null;
	    try {
			is = schemaUrl.openStream();
			schemaFile = new StreamSource(is);
		    Schema schema = factory.newSchema(schemaFile);
		    
		    javax.xml.validation.Validator validator = schema.newValidator();
		    validator.validate(new DOMSource(document));
		    is.close();
		    return true;
		    
		} catch (IOException e3) {
			logger.error(e3.getMessage());
		} catch (SAXException e) {
			logger.error(e.getMessage());
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
		
		return false;
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
		boolean xmlValid = validateXml(doc, Validator.jobRequestSchema);
		if (xmlValid == false) return false;
		
		boolean argsValid = validateArguments(doc, application);
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
	private static boolean validateArguments(Document doc, PipelineWebService application) {
		
		Element scriptElm = (Element)doc.getElementsByTagName("script").item(0);
		URI scriptUri = null;
		try {
			scriptUri = new URI(scriptElm.getAttribute("href"));
		}
		catch (URISyntaxException e) {
			logger.error(e.getMessage());
			return false;
		}
		
		ScriptRegistry scriptRegistry = application.getScriptRegistry();
		XProcScriptService unfilteredScript = scriptRegistry.getScript(scriptUri);
		
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
		boolean hasAllRequiredInputs = validateInputPortData(script.getXProcPipelineInfo().getInputPorts(), 
				doc.getElementsByTagName("input"), script);
		// options
		boolean hasAllRequiredOptions = validateOptionData(script.getXProcPipelineInfo().getOptions(), 
				doc.getElementsByTagName("option"), script);
		// outputs (if run in local mode)
		boolean hasAllRequiredOutputs = validateOutputPortData(script
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
	private static boolean validateOptionData(Iterable<XProcOptionInfo> options, NodeList nodes, XProcScript script) {
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
	private static boolean validateInputPortData(Iterable<XProcPortInfo> ports, NodeList nodes, XProcScript script) {
		
		Iterator<XProcPortInfo>it = ports.iterator();
		boolean hasAllRequiredArgs = true;
		while (it.hasNext()) {
			XProcPortInfo arg = it.next();
			boolean validArg = false;
			// input elements should be of one of two forms:
			// <input name="in1">
			//   <file src="./path/to/file/book.xml/>
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
					// <input> elements will have either <file> element children or <docwrapper> element children
					NodeList fileNodes = elm.getElementsByTagName("file");
					NodeList docwrapperNodes = elm.getElementsByTagName("docwrapper");
					
					if (fileNodes.getLength() == 0 && docwrapperNodes.getLength() == 0) {
						validArg = false;
					}
					else {
						if (fileNodes.getLength() > 0) {
							validArg = validateFileElements(fileNodes);
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
	private static boolean validateOutputPortData(Iterable<XProcPortInfo> ports, NodeList nodes, XProcScript script) {
		

		Iterator<XProcPortInfo>it = ports.iterator();
		boolean hasAllRequiredArgs = true;
		
		while (it.hasNext()) {
			XProcPortInfo arg = it.next();
			boolean validArg = false;
			for (int i=0; i<nodes.getLength(); i++) {
				Element elm = (Element)nodes.item(i);
				if (elm.getAttribute("name").equals(arg.getName())) {
					NodeList fileNodes = elm.getElementsByTagName("file");
					
					if (fileNodes.getLength() == 0) {
						validArg = false;
					}
					else {
						if (fileNodes.getLength() > 0) {
							validArg = validateFileElements(fileNodes);
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
			String xml = XmlFormatter.nodeToString(content);
			isValid &= validateWellFormedXml(xml);
		}
		
		return isValid;
	}

	// make sure these @src attributes are non-empty
	// nodes must contain at least one item
	// all nodes must be <file> elements
	/**
	 * Validate file elements.
	 *
	 * @param nodes the nodes
	 * @return true, if successful
	 */
	private static boolean validateFileElements(NodeList nodes) {
		boolean isValid = true;
		
		for (int i = 0; i<nodes.getLength(); i++) {
			Element elm = (Element)nodes.item(i);
			isValid &= elm.getAttribute("src").trim().length() > 0;
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
		// TODO what are all the possibilities for mediaType?
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
