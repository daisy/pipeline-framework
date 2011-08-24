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
    public Representation createResource(Representation entity) throws Exception {
        if (entity == null) {
        	// POST request with no entity.
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            return null;
        }
        
        if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), true)) {
            Request request = this.getRequest();
            // sort through the multipart request
            MultipartRequestData data = processMultipart(request);
            
			boolean isValid = Validator.validateJobRequest(data.getXml(), (PipelineWebService)this.getApplication());
			
			if (!isValid) {
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return null;
			}

			Job job = createJob(data.getXml(), data.getZipFile());
			
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
        else {
        	// TODO deal with inline XML
        	return null;
        }
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
	                // TODO do i have to reopen the file first?
	                zip = new ZipFile(file); 
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

		Element scriptElm = (Element) doc.getElementsByTagName("useScript").item(0);
		
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
		
		// iterate through the input nodes and fill in the builder values
		NodeList inputNodes = doc.getElementsByTagName("input");
		for (int i = 0; i < inputNodes.getLength(); i++) {
			Element inputElm = (Element) inputNodes.item(i);
			String name = inputElm.getAttribute("name");
			
			
			NodeList fileNodes = inputElm.getElementsByTagName("file");
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
	
		NodeList optionNodes = doc.getElementsByTagName("option");
		for (int i = 0; i< optionNodes.getLength(); i++) {
			Element optionElm = (Element) optionNodes.item(i);
			String name = optionElm.getAttribute("name");
			String val = optionElm.getTextContent();
			builder.withOption(new QName(name), val);
		}
		
		XProcInput input = builder.build();
		ResourceCollection resourceCollection = new ZipResourceContext(zip);
		JobManager jobMan = ((PipelineWebService)this.getApplication()).getJobManager();
		Job job = jobMan.newJob(script, input, resourceCollection);
		return  job;
	}
}
