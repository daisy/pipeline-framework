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

import org.daisy.pipeline.DaisyPipelineContext;
import org.daisy.pipeline.modules.converter.ConverterArgument;
import org.daisy.pipeline.modules.converter.ConverterArgument.Direction;
import org.daisy.pipeline.modules.converter.ConverterDescriptor;
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
	
	public static boolean validateJobRequest(Document doc, DaisyPipelineContext context) {
		boolean xml_valid = validateXml(doc, Validator.jobRequestSchema);
		if (xml_valid == false) {
			return false;
		}
		
		// now check that there are the right number of arguments
		Element useConverterElm = (Element)doc.getElementsByTagName("useConverter").item(0);
		String converterUri = useConverterElm.getAttribute("href");
		
		ConverterDescriptor converterDescriptor;
		try {
			converterDescriptor = context.getConverterRegistry().getDescriptor(new URI(converterUri));
			//converterDescriptor = context.getConverterRegistry().getDescriptor(new URI(converterUri));
			
			if (converterDescriptor != null) {
				// make sure that each converter argument is fulfilled as required
				Iterator<ConverterArgument>it = converterDescriptor.getConverter().getArguments().iterator();
				NodeList inputNodes = useConverterElm.getElementsByTagName("input");
				
				boolean hasAllRequiredArgs = true;
				while (it.hasNext()) {
					ConverterArgument arg = it.next();
					
					boolean foundArg = false;
					
					// if this is a required argument and not an output argument
					// TODO: filter out arguments that have @dir = output
					// below, we just filter out arguments with ConverterArgument.Type.OUTPUT
					if (arg.isOptional() == false && arg.getDirection() != Direction.OUTPUT) {
						// look through the jobRequest input elements to see if there's one to match this argument
						// also check that its contents are non-empty
						for (int i=0; i<inputNodes.getLength(); i++) {
							Element elm = (Element)inputNodes.item(i);
							if (elm.getAttribute("name") == arg.getName() && elm.getTextContent().trim().length() > 0) {
								foundArg = true;
							}
						}
					}    
					else {
						// if the arg is optional, we don't care if it's present or not
						foundArg = true;
					}
					
					hasAllRequiredArgs |= foundArg;
				}
				
				if (hasAllRequiredArgs == false) {
					System.out.print("ERROR: Required args missing");
				}
				return hasAllRequiredArgs;
			}
			else {
				System.out.print("ERROR: Converter not found");
				return false;
			}
		}  catch (URISyntaxException e) {
			System.out.print("ERROR: Malformed URI");
			return false;
		}
	}

	
	

}
