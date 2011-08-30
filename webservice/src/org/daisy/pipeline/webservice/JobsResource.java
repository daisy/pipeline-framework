package org.daisy.pipeline.webservice;

import java.io.File;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipFile;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.daisy.common.base.Provider;
import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcOptionInfo;
import org.daisy.common.xproc.XProcPortInfo;
import org.daisy.pipeline.job.DefaultJobManager;
import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.job.JobManager;
import org.daisy.pipeline.job.ResourceCollection;
import org.daisy.pipeline.job.ZipResourceContext;
import org.daisy.pipeline.script.ScriptRegistry;
import org.daisy.pipeline.script.XProcScript;
import org.daisy.pipeline.script.XProcScriptService;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class JobsResource extends ServerResource {

	// TODO make configurable
	private String tempfileDir = "/tmp/";
	private String tempfilePrefix = "p2ws";
	private String tempfileSuffix = ".zip";
	
	@Get("xml")
	public Representation getResource() {
		String serverAddress = ((PipelineWebService) this.getApplication()).getServerAddress();
		DefaultJobManager jobMan = new DefaultJobManager(); 
		Document doc = XmlFormatter.jobsToXml(jobMan.getJobs(), serverAddress);
		DomRepresentation dom = new DomRepresentation(MediaType.APPLICATION_XML, doc);
		setStatus(Status.SUCCESS_OK);
		return dom;
	}

	/*
	 * taken from an example at:
	 * http://wiki.restlet.org/docs_2.0/13-restlet/28-restlet/64-restlet.html
	 */
	@Post
    public Representation createResource(Representation representation) throws Exception {
        if (representation == null) {
        	// POST request with no entity.
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            return null;
        }
        
        Document doc = null;
        ZipFile zipfile = null;
        
        if (MediaType.MULTIPART_FORM_DATA.equals(representation.getMediaType(), true)) {
            Request request = this.getRequest();
            // sort through the multipart request
            MultipartRequestData data = processMultipart(request);
            
            doc = data.getXml();
            zipfile = data.getZipFile();
        }
     // else it's not multipart; all data should be inline.
        else {
        	String s = representation.getText();
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(s));
            doc = builder.parse(is);
        }
        
            
		boolean isValid = Validator.validateJobRequest(doc, (PipelineWebService)this.getApplication());
			
		if (!isValid) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return null;
		}

		Job job = createJob(doc, zipfile);
		
		if (job == null) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return null;
		}
		JobId id = job.getId();
		
		// return the URI of the new job
		Representation newJobUriRepresentation = new EmptyRepresentation();
		String serverAddress = ((PipelineWebService) this.getApplication()).getServerAddress();
		newJobUriRepresentation.setLocationRef(serverAddress + "/jobs/" + id.toString());

		setStatus(Status.SUCCESS_CREATED);
		return newJobUriRepresentation;
        
    }
	
	private MultipartRequestData processMultipart(Request request) {
		// 1/ Create a factory for disk-based file items
        DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
        fileItemFactory.setSizeThreshold(1000240);

        // 2/ Create a new file upload handler based on the Restlet
        // FileUpload extension that will parse Restlet requests and
        // generates FileItems.
        RestletFileUpload upload = new RestletFileUpload(fileItemFactory);
        List<FileItem> items;

        ZipFile zip = null;
        String xml = "";
        try {
			items = upload.parseRequest(request);
			Iterator<FileItem> it = items.iterator();
	        while (it.hasNext()) {
	            FileItem fi = it.next();
	            if (fi.getFieldName().equals("jobData")) {
	            	File file = File.createTempFile(tempfilePrefix, tempfileSuffix, new File(tempfileDir));
	                fi.write(file);	  
	                
	                // re-opening the file after writing to it
	                File file2 = new File(file.getAbsolutePath());
	                zip = new ZipFile(file2); 
	            }
	            else if (fi.getFieldName().equals("jobRequest")) {
	            	xml = fi.getString("utf-8");
	            }
	        }

	        if (zip == null || xml.length() == 0) {
	        	setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
	        	return null;
	        }
	        
	        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			docFactory.setNamespaceAware(true);
			DocumentBuilder builder = docFactory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(xml));
			Document doc = builder.parse(is);

	        MultipartRequestData data = new MultipartRequestData(zip, doc);
	        return data;
	        
		} catch (FileUploadException e) {
			// TODO log an error
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			// TODO log an error
			e.printStackTrace();
			return null;
		}
	}
	
	// just a convenience class for representing the parts of a multipart request
	private class MultipartRequestData {
		private ZipFile zip;
		private Document xml;
		
		MultipartRequestData(ZipFile zip, Document xml) {
			this.zip = zip;
			this.xml = xml;
		}
		
		ZipFile getZipFile() {
			return zip;
		}
		
		Document getXml() {
			return xml;
		}
	}
	
	private Job createJob(Document doc, ZipFile zip) {

		Element scriptElm = (Element) doc.getElementsByTagName("script").item(0);
		
		URI scriptUri = null;
		try {
			scriptUri = new URI(scriptElm.getAttribute("href"));
		} catch (URISyntaxException e) {
			// TODO log an error
			e.printStackTrace();
			return null;
		}
		
		// get the script from the URI
		ScriptRegistry scriptRegistry = ((PipelineWebService)this.getApplication()).getScriptRegistry();
		XProcScriptService scriptService = scriptRegistry.getScript(scriptUri);
		
		if (scriptService == null) {
			return null;
		}
		
		XProcScript script = scriptService.load();
		XProcInput.Builder builder = new XProcInput.Builder(script.getXProcPipelineInfo());
		
		addInputsToJob(doc.getElementsByTagName("input"), script.getXProcPipelineInfo().getInputPorts(), builder);
		addOptionsToJob(doc.getElementsByTagName("option"), script.getXProcPipelineInfo().getOptions(), builder);
		
		XProcInput input = builder.build();
		
		JobManager jobMan = ((PipelineWebService)this.getApplication()).getJobManager();
		Job job = null;
		if (zip != null){
			ResourceCollection resourceCollection = new ZipResourceContext(zip);
			job = jobMan.newJob(script, input, resourceCollection);
		}
		else {
			job = jobMan.newJob(script, input);
		}
		
		return  job;
	}

	private void addInputsToJob(NodeList nodes, Iterable<XProcPortInfo> inputPorts, XProcInput.Builder builder) {
		
		Iterator<XProcPortInfo> it = inputPorts.iterator();
		while (it.hasNext()) {
			XProcPortInfo input = it.next();
			String inputName = input.getName();
			for (int i = 0; i < nodes.getLength(); i++) {
				Element inputElm = (Element) nodes.item(i);
				String name = inputElm.getAttribute("name");
				if (name.equals(inputName)) {
					NodeList fileNodes = inputElm.getElementsByTagName("file");
					NodeList docwrapperNodes = inputElm.getElementsByTagName("docwrapper");
				
					if (fileNodes.getLength() > 0) {
						for (int j = 0; j < fileNodes.getLength(); j++) {
							String src = ((Element)fileNodes.item(j)).getAttribute("src");
							final SAXSource source = new SAXSource();
				            source.setSystemId(src);
				            Provider<Source> prov= new Provider<Source>(){
				            	@Override
				                public Source provide(){
				            		return source;
				            	}
				            };
			
							builder.withInput(name, prov);
						}
					}
					else {
						for (int j = 0; j< docwrapperNodes.getLength(); j++){
							Element docwrapper = (Element)docwrapperNodes.item(j);
							Node content = null;
							// find the first element child
							for (int q = 0; q < docwrapper.getChildNodes().getLength(); q++) {
								if (docwrapper.getChildNodes().item(q).getNodeType() == Node.ELEMENT_NODE) {
									content = docwrapper.getChildNodes().item(q);
									break;
								}
							}
							
							final SAXSource source = new SAXSource();
				            
							// TODO any way to get Source directly from a node?
							String xml = XmlFormatter.nodeToString(content);
				            InputSource is = new org.xml.sax.InputSource(new java.io.StringReader(xml));
							source.setInputSource(is);
				            Provider<Source> prov= new Provider<Source>(){
				            	@Override
				                public Source provide(){
				            		return source;
				            	}
				            };
				            builder.withInput(name, prov);
						}
					}
				}
			}
		}
		
	}
	
	private void addOptionsToJob(NodeList nodes, Iterable<XProcOptionInfo> options, XProcInput.Builder builder) {
		
		Iterator<XProcOptionInfo> it = options.iterator();
		while(it.hasNext()) {
			XProcOptionInfo opt = it.next();
			String optionName = opt.getName().toString();
			
			// look for name
			boolean found = false;
			for (int i = 0; i< nodes.getLength(); i++) {
				Element optionElm = (Element) nodes.item(i);
				String name = optionElm.getAttribute("name");
				if (name.equals(optionName)) {
					String val = optionElm.getTextContent();
					builder.withOption(new QName(name), val);
					found = true;
					break;
				}
			}
			
			// if the name was not found, as would be the case for optional options or those filtered out
			if (!found) {
				builder.withOption(new QName(optionName), "");
			}
		}
	}
}
