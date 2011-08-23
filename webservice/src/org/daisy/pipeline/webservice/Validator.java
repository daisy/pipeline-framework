package org.daisy.pipeline.webservice;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.daisy.common.xproc.XProcOptionInfo;
import org.daisy.common.xproc.XProcPortInfo;
import org.daisy.pipeline.job.XProcInfoFilter;
import org.daisy.pipeline.script.ScriptRegistry;
import org.daisy.pipeline.script.XProcScript;
import org.daisy.pipeline.script.XProcScriptService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class Validator {
	
	// although in everyday practice, the function validateJobRequest will be the most-used, 
	// all the schema URLs are included here so that during testing, the web service can validate
	// its own output by calling validateXml with the appropriate schema URL.
	public static final URL converterSchema = Validator.class.getResource("resources/converter.xsd");
	public static final URL convertersSchema = Validator.class.getResource("resources/converters.xsd");
	public static final URL jobSchema = Validator.class.getResource("resources/job.xsd");
	public static final URL jobRequestSchema = Validator.class.getResource("resources/jobRequest.xsd");
	public static final URL jobsSchema = Validator.class.getResource("resources/jobs.xsd");
	
	
	// If the Document isn't namespace-aware, this will likely fail
	public static boolean validateXml(Document document, URL schemaUrl) {
	    
		if (document == null || schemaUrl == null) {
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
			// TODO Auto-generated catch block
			e3.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return false;
	}
	
	public static boolean validateJobRequest(Document doc, PipelineWebService application) {
		
		// validate against the schema
		boolean xml_valid = validateXml(doc, Validator.jobRequestSchema);
		if (xml_valid == false) {
			return false;
		}
		
		// now check that there are the right number of arguments
		Element useConverterElm = (Element)doc.getElementsByTagName("useConverter").item(0);
		String scriptUri = useConverterElm.getAttribute("href");
		
		try {
			ScriptRegistry scriptRegistry = application.getScriptRegistry();
			XProcScriptService unfilteredScript = scriptRegistry.getScript(new URI(scriptUri));
			
			if (unfilteredScript == null) {
				System.out.println("ERROR: Script not found");
				return false;
			}
			
			XProcScript script = XProcInfoFilter.INSTANCE.filterScript(unfilteredScript.load());
			
			// inputs
			boolean hasAllRequiredInputs = validatePortData(script.getXProcPipelineInfo().getInputPorts(), 
					useConverterElm.getElementsByTagName("input"));
			// options
			boolean hasAllRequiredOptions = validateOptionData(script.getXProcPipelineInfo().getOptions(), 
					useConverterElm.getElementsByTagName("option"));
			// outputs
			// TODO do we ever require outputs??
				
				return hasAllRequiredInputs & hasAllRequiredOptions;
		}  catch (URISyntaxException e) {
			System.out.print("ERROR: Malformed URI");
			return false;
		}
	}

	private static boolean validateOptionData(Iterable<XProcOptionInfo> options, NodeList nodes) {
		Iterator<XProcOptionInfo>it = options.iterator();
		boolean hasAllRequiredArgs = true;
		while (it.hasNext()) {
			XProcOptionInfo arg = it.next();
			// skip optional arguments
			if (arg.isRequired() == false) {
				continue;
			}
			boolean foundArg = false;
			
			for (int i=0; i<nodes.getLength(); i++) {
				Element elm = (Element)nodes.item(i);
				if (elm.getAttribute("name") == arg.getName().toString() && elm.getTextContent().trim().length() > 0) {
					// TODO: validate as whatever its type should be
					foundArg = true;
				}
			}
			hasAllRequiredArgs |= foundArg;
		}
		
		if (hasAllRequiredArgs == false) {
			// TODO: be more specific
			System.out.print("ERROR: Required args missing");
		}
		return hasAllRequiredArgs;
	}

	private static boolean validatePortData(Iterable<XProcPortInfo> ports, NodeList nodes) {
		
		Iterator<XProcPortInfo>it = ports.iterator();
		boolean hasAllRequiredArgs = true;
		while (it.hasNext()) {
			XProcPortInfo arg = it.next();
			boolean foundArg = false;
			
			for (int i=0; i<nodes.getLength(); i++) {
				Element elm = (Element)nodes.item(i);
				if (elm.getAttribute("name") == arg.getName() && elm.getTextContent().trim().length() > 0) {
					// TODO: validate as XML
					foundArg = true;
				}
			}
			hasAllRequiredArgs |= foundArg;
		}
		
		if (hasAllRequiredArgs == false) {
			// TODO: be more specific
			System.out.print("ERROR: Required args missing");
		}
		return hasAllRequiredArgs;
	}

	
	

}
