package org.daisy.pipeline.webserviceutils.xml;

import org.daisy.pipeline.job.JobSize;
import org.daisy.pipeline.webserviceutils.Routes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class JobsSizeXmlWriter{
        Iterable<? extends JobSize> sizes; 
        private static final Logger logger = LoggerFactory.getLogger(JobsSizeXmlWriter.class);
	public JobsSizeXmlWriter(Iterable<? extends JobSize> sizes) {
		this.sizes = sizes;
	}
	
	public Document getXmlDocument() {
		if (this.sizes== null) {
			logger.warn("Could not create XML for null jobs");
			return null;
		}
		return sizesToXml(this.sizes);
	}
	
	private static Document sizesToXml(Iterable<? extends JobSize> sizes) {
		String baseUri = new Routes().getBaseUri();
		Document doc = XmlUtils.createDom("jobSizes");
		Element sizesElm = doc.getDocumentElement();
		sizesElm.setAttribute("href", baseUri + Routes.SIZES_ROUTE);
		
		for (JobSize size : sizes) {
			JobSizeXmlWriter writer = new JobSizeXmlWriter(size);
			writer.addAsElementChild(sizesElm);
		}
		
		// for debugging only
		//if (!XmlValidator.validate(doc, XmlValidator.JOBS_SCHEMA_URL)) {
			//logger.error("INVALID XML:\n" + XmlUtils.DOMToString(doc));
		//}

		return doc;
	}
        
}
