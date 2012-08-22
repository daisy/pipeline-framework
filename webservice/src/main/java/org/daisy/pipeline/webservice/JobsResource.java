package org.daisy.pipeline.webservice;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipFile;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.daisy.common.base.Provider;
import org.daisy.common.transform.LazySaxSourceProvider;
import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcOptionInfo;
import org.daisy.common.xproc.XProcPortInfo;
import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.JobManager;
import org.daisy.pipeline.job.ResourceCollection;
import org.daisy.pipeline.job.ZipResourceContext;
import org.daisy.pipeline.script.ScriptRegistry;
import org.daisy.pipeline.script.XProcOptionMetadata;
import org.daisy.pipeline.script.XProcScript;
import org.daisy.pipeline.script.XProcScriptService;
import org.daisy.pipeline.webserviceutils.callback.Callback;
import org.daisy.pipeline.webserviceutils.callback.Callback.CallbackType;
import org.daisy.pipeline.webserviceutils.clients.Client;
import org.daisy.pipeline.webserviceutils.xml.JobXmlWriter;
import org.daisy.pipeline.webserviceutils.xml.JobsXmlWriter;
import org.daisy.pipeline.webserviceutils.xml.XmlUtils;
import org.daisy.pipeline.webserviceutils.xml.XmlWriterFactory;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
// TODO: Auto-generated Javadoc
/**
 * The Class JobsResource.
 */
public class JobsResource extends AuthenticatedResource {

	/** The tempfile prefix. */
	private final String tempfilePrefix = "p2ws";
	private final String tempfileSuffix = ".zip";

	private final String JOB_DATA_FIELD = "job-data";
	private final String JOB_REQUEST_FIELD = "job-request";

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(JobsResource.class.getName());

	/**
	 * Gets the resource.
	 *
	 * @return the resource
	 */
	@Get("xml")
	public Representation getResource() {
		if (!isAuthenticated()) {
		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
		return null;
	}
		JobManager jobMan = webservice().getJobManager();
		JobsXmlWriter writer = XmlWriterFactory.createXmlWriter(jobMan.getJobs());
		Document doc = writer.getXmlDocument();
		DomRepresentation dom = new DomRepresentation(MediaType.APPLICATION_XML, doc);
		setStatus(Status.SUCCESS_OK);
		return dom;
	}


	/**
	 * Creates the resource.
	 *
	 * @param representation the representation
	 * @return the representation
	 * @throws Exception the exception
	 */
	@Post
    public Representation createResource(Representation representation) {
		if (!isAuthenticated()) {
		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
		return null;
	}
		Client client = null;
		if (webservice().isAuthenticationEnabled()) {
			String clientId = getQuery().getFirstValue("authid");
			client = webservice().getClientStore().get(clientId);
		}

        if (representation == null) {
		// POST request with no entity.
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            return null;
        }

        
        Document doc = null;
        ZipFile zipfile = null;

        if (MediaType.MULTIPART_FORM_DATA.equals(representation.getMediaType(), true)) {
            Request request = getRequest();
            // sort through the multipart request
            MultipartRequestData data = processMultipart(request);
            if (data == null) {
		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		return null;
            }
            doc = data.getXml();
            zipfile = data.getZipFile();
        }
     // else it's not multipart; all data should be inline.
        else {
		String s;
			try {
				s = representation.getText();
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    factory.setNamespaceAware(true);
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    InputSource is = new InputSource(new StringReader(s));
		    doc = builder.parse(is);
			} catch (IOException e) {
				logger.error(e.getMessage());
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return null;
			} catch (ParserConfigurationException e) {
				logger.error(e.getMessage());
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return null;
			} catch (SAXException e) {
				logger.error(e.getMessage());
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return null;
			}
        }

		boolean isValid = Validator.validateJobRequest(doc, webservice());

		if (!isValid) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return null;
		}

		Job job = createJob(doc, zipfile, client);

		if (job == null) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return null;
		}

		JobXmlWriter writer = XmlWriterFactory.createXmlWriter(job);
		Document jobXml = writer.withAllMessages().withScriptDetails().getXmlDocument();
		DomRepresentation dom = new DomRepresentation(MediaType.APPLICATION_XML, jobXml);
		setStatus(Status.SUCCESS_CREATED);
		return dom;

    }

	/*
	 * taken from an example at:
	 * http://wiki.restlet.org/docs_2.0/13-restlet/28-restlet/64-restlet.html
	 */
	/**
	 * Process multipart.
	 *
	 * @param request the request
	 * @return the multipart request data
	 */
	private MultipartRequestData processMultipart(Request request) {

		String tmpdir = webservice().getTmpDir();

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
		    if (fi.getFieldName().equals(JOB_DATA_FIELD)) {
			File file = File.createTempFile(tempfilePrefix, tempfileSuffix, new File(tmpdir));
			fi.write(file);

			// re-opening the file after writing to it
			File file2 = new File(file.getAbsolutePath());
			zip = new ZipFile(file2);
		    }
		    else if (fi.getFieldName().equals(JOB_REQUEST_FIELD)) {
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
			logger.error(e.getMessage());
			return null;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	// just a convenience class for representing the parts of a multipart request
	/**
	 * The Class MultipartRequestData.
	 */
	private class MultipartRequestData {

	/**
	 * Process multipart.
	 *
	 * @param request the request
	 * @return the multipart request data
	 */
		/** The zip. */
		private final ZipFile zip;

		/** The xml. */
		private final Document xml;

		/**
		 * Instantiates a new multipart request data.
		 *
		 * @param zip the zip
		 * @param xml the xml
		 */
		MultipartRequestData(ZipFile zip, Document xml) {
			this.zip = zip;
			this.xml = xml;
		}

		/**
		 * Gets the zip file.
		 *
		 * @return the zip file
		 */
		ZipFile getZipFile() {
			return zip;
		}

		/**
		 * Gets the xml.
		 *
		 * @return the xml
		 */
		Document getXml() {
			return xml;
		}
	}

	/**
	 * Creates the job.
	 *
	 * @param doc the doc
	 * @param zip the zip
	 * @return the job
	 */
	private Job createJob(Document doc, ZipFile zip, Client client) {

		Element scriptElm = (Element) doc.getElementsByTagName("script").item(0);

		// TODO eventually we might want to have an href-script ID lookup table
		// but for now, we'll get the script ID from the last part of the URL
		String scriptId = scriptElm.getAttribute("href");
		if (scriptId.endsWith("/")) {
		    scriptId = scriptId.substring(0, scriptId.length() - 1);
		}
		int idx = scriptId.lastIndexOf('/');
		scriptId = scriptId.substring(idx+1);

		// get the script from the ID
		ScriptRegistry scriptRegistry = webservice().getScriptRegistry();
		XProcScriptService unfilteredScript = scriptRegistry.getScript(scriptId);
		if (unfilteredScript == null) {
			logger.error("Script not found");
			return null;
		}
		XProcScript script = unfilteredScript.load();
		XProcInput.Builder builder = new XProcInput.Builder(script.getXProcPipelineInfo());

		addInputsToJob(doc.getElementsByTagName("input"), script.getXProcPipelineInfo().getInputPorts(), builder);

		/*Iterable<XProcOptionInfo> filteredOptions = null;
		if (!((PipelineWebService) getApplication()).isLocal()) {
			filteredOptions = XProcScriptFilter.INSTANCE.filter(script).getXProcPipelineInfo().getOptions();
		}*/

		addOptionsToJob(doc.getElementsByTagName("option"), script, builder);// script.getXProcPipelineInfo().getOptions(), builder, filteredOptions);

		XProcInput input = builder.build();

		JobManager jobMan = webservice().getJobManager();
		Job job = null;
		if (zip != null){
			ResourceCollection resourceCollection = new ZipResourceContext(zip);
			job = jobMan.newJob(script, input, resourceCollection);
		}
		else {
			job = jobMan.newJob(script, input);
		}

		NodeList callbacks = doc.getElementsByTagName("callback");
		for (int i = 0; i<callbacks.getLength(); i++) {
			Element elm = (Element)callbacks.item(i);
			String href = elm.getAttribute("href");
			CallbackType type = CallbackType.valueOf(elm.getAttribute("type").toUpperCase());
			String frequency = elm.getAttribute("frequency");
			Callback callback = null;
			int freq = 0;
			if (frequency.length() > 0) {
				freq = Integer.parseInt(frequency);
			}

			try {
				callback = new Callback(job.getId(), client, new URI(href), type, freq);
			} catch (URISyntaxException e) {
				logger.warn("Cannot create callback: " + e.getMessage());
			}

			if (callback != null) {
				webservice().getCallbackRegistry().addCallback(callback);
			}
		}
		return job;
	}

	/**
	 * Adds the inputs to job.
	 *
	 * @param nodes the nodes
	 * @param inputPorts the input ports
	 * @param builder the builder
	 */
	private void addInputsToJob(NodeList nodes, Iterable<XProcPortInfo> inputPorts, XProcInput.Builder builder) {

		Iterator<XProcPortInfo> it = inputPorts.iterator();
		while (it.hasNext()) {
			XProcPortInfo input = it.next();
			String inputName = input.getName();
			for (int i = 0; i < nodes.getLength(); i++) {
				Element inputElm = (Element) nodes.item(i);
				String name = inputElm.getAttribute("name");
				if (name.equals(inputName)) {
					NodeList fileNodes = inputElm.getElementsByTagName("item");
					NodeList docwrapperNodes = inputElm.getElementsByTagName("docwrapper");

					if (fileNodes.getLength() > 0) {
						for (int j = 0; j < fileNodes.getLength(); j++) {
							String src = ((Element)fileNodes.item(j)).getAttribute("value");
//							Provider<Source> prov= new Provider<Source>(){
//								@Override
//								public Source provide(){
//									SAXSource source = new SAXSource();
//									source.setSystemId(new String(src.getBytes()));
//									return source;
//								}
//							};
							LazySaxSourceProvider prov= new LazySaxSourceProvider(src);
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
							String xml = XmlUtils.nodeToString(content);
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

	/**
	 * Adds the options to job.
	 */
	//private void addOptionsToJob(NodeList nodes, Iterable<XProcOptionInfo> allOptions, XProcInput.Builder builder, Iterable<XProcOptionInfo> filteredOptions) {
	private void addOptionsToJob(NodeList nodes, XProcScript script, XProcInput.Builder builder) {

		Iterable<XProcOptionInfo> allOptions = script.getXProcPipelineInfo().getOptions();

		Iterable<XProcOptionInfo> filteredOptions = null;
		if (!webservice().isLocal()) {
			filteredOptions = XProcScriptFilter.INSTANCE.filter(script).getXProcPipelineInfo().getOptions();
		}

		Iterator<XProcOptionInfo> it = allOptions.iterator();
		while(it.hasNext()) {
			XProcOptionInfo opt = it.next();
			String optionName = opt.getName().toString();

			// if we are filtering options, then check to ensure that this particular option exists in the filtered set
			if (filteredOptions != null) {
				Iterator<XProcOptionInfo> itFilter = filteredOptions.iterator();
				boolean found = false;
				while (itFilter.hasNext()) {
					String filteredOptName = itFilter.next().getName().toString();
					if (filteredOptName.equals(optionName)) {
						found = true;
						break;
					}
				}

				// if the option did not exist in the filtered set of options
				// then we are not allowed to set it
				// however, it still requires a value, so set it to ""
				if (!found) {
					builder.withOption(new QName(optionName), "");
					continue;
				}
			}

			// this is an option we are allowed to set. so, look for the option in the job request doc.
			for (int i = 0; i< nodes.getLength(); i++) {
				Element optionElm = (Element) nodes.item(i);
				String name = optionElm.getAttribute("name");
				if (name.equals(optionName)) {
					XProcOptionMetadata metadata = script.getOptionMetadata(new QName(name));
					if (metadata.isSequence()) {
						NodeList items = optionElm.getElementsByTagName("item");
						// concat items
						String val = ((Element)items.item(0)).getAttribute("value");
						for (int j = 1; j<items.getLength(); j++) {
							Element e = (Element)items.item(j);
							val += metadata.getSeparator() + e.getAttribute("value");
						}
						builder.withOption(new QName(name), val);
					}
					else {
						String val = optionElm.getTextContent();
                        builder.withOption(new QName(name), val);
						break;
					}

				}
			}
		}
	}
}
