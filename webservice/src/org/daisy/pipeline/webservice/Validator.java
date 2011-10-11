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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class Validator {
	
	// although in everyday practice, the function validateJobRequest will be the most-used, 
	// all the schema URLs are included here so that during testing, the web service can validate
	// its own output by calling validateXml with the appropriate schema URL.
	public static final URL scriptSchema = Validator.class.getResource("resources/script.xsd");
	public static final URL scriptsSchema = Validator.class.getResource("resources/scripts.xsd");
	public static final URL jobSchema = Validator.class.getResource("resources/job.xsd");
	public static final URL jobRequestSchema = Validator.class.getResource("resources/jobRequest.xsd");
	public static final URL jobsSchema = Validator.class.getResource("resources/jobs.xsd");
	
	// If the Document isn't namespace-aware, this will likely fail
	public static boolean validateXml(Document document, URL schemaUrl) {
	    
		if (document == null) {
			System.out.println("Could not validate null document");
			return false;
		}
		
		if (schemaUrl == null) {
			System.out.println("Could not validate -- no schema given.");
			return false;
		}
		
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
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
			// TODO log an error
			e3.printStackTrace();
		} catch (SAXException e) {
			// TODO log an error
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				// TODO log an error
				e.printStackTrace();
			}
		}
		
		return false;
	}
	
	public static boolean validateJobRequest(Document doc, PipelineWebService application) {
		
		// validate against the schema
		boolean xmlValid = validateXml(doc, Validator.jobRequestSchema);
		if (xmlValid == false) return false;
		
		boolean argsValid = validateArguments(doc, application);
		return argsValid;
	}
	
	// check that there is a value for each required argument
	// check data of the argument value to the fullest extent possible
	private static boolean validateArguments(Document doc, PipelineWebService application) {
		
		Element scriptElm = (Element)doc.getElementsByTagName("script").item(0);
		URI scriptUri = null;
		try {
			scriptUri = new URI(scriptElm.getAttribute("href"));
		}
		catch (URISyntaxException e) {
			System.out.print("ERROR: Malformed URI");
			return false;
		}
		
		ScriptRegistry scriptRegistry = application.getScriptRegistry();
		XProcScriptService unfilteredScript = scriptRegistry.getScript(scriptUri);
		
		if (unfilteredScript == null) {
			System.out.println("ERROR: Script not found");
			return false;
		}
		
		XProcScript script = XProcScriptFilter.INSTANCE.filter(unfilteredScript.load());
		
		// inputs
		boolean hasAllRequiredInputs = validateInputPortData(script.getXProcPipelineInfo().getInputPorts(), 
				doc.getElementsByTagName("input"), script);
		// options
		boolean hasAllRequiredOptions = validateOptionData(script.getXProcPipelineInfo().getOptions(), 
				doc.getElementsByTagName("option"), script);
		
		// note that we don't validate output ports because it doesn't make sense in the context of the web service
		
		return hasAllRequiredInputs & hasAllRequiredOptions;
	}

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
			System.out.println("ERROR: Required args missing");
		}
		return hasAllRequiredArgs;
	}

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
			System.out.println("ERROR: Required args missing");
		}
		return hasAllRequiredArgs;
	}
	
	// make sure these nodes contain well-formed XML
	// nodes must contain at least one item
	// nodes must be <docwrapper> elements
	// TODO incorporate media type
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
	private static boolean validateFileElements(NodeList nodes) {
		boolean isValid = true;
		
		for (int i = 0; i<nodes.getLength(); i++) {
			Element elm = (Element)nodes.item(i);
			isValid &= elm.getAttribute("src").trim().length() > 0;
		}
		return isValid;
	}

	private static boolean validateOptionType(String value, String mediaType) {
		// TODO what are all the possibilities for mediaType?
		// for now, just check that the string is non-empty
		return value.trim().length() > 0;
	}
	
	// just validate whether the xml is well-formed or not.  
	// we don't verify flavor of xml is expected; 
	// that's expected to be handled by the xproc script itself
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
			e.printStackTrace();
			return false;
		} catch (SAXException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}		
		return true;
	}
}
