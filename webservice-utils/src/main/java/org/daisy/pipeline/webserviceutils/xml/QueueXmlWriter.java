package org.daisy.pipeline.webserviceutils.xml;

import java.util.Collection;

import org.daisy.pipeline.webserviceutils.Routes;
import org.daisy.pipeline.job.PrioritizedJob;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class QueueXmlWriter{
                
        Iterable<PrioritizedJob> jobs;
        /**
         * @param size
         */
        public QueueXmlWriter(Iterable<PrioritizedJob> jobs) {
                this.jobs= jobs;
        }

        public Document getXmlDocument(){
                Document doc = XmlUtils.createDom("queue");
		String baseUri = new Routes().getBaseUri();
                Element queueElem= doc.getDocumentElement();
		queueElem.setAttribute("href", baseUri + Routes.QUEUE_ROUTE);
                for (PrioritizedJob job: this.jobs){
                        addElementData(job, queueElem);

                }
                
                // for debugging only
                //if (!XmlValidator.validate(doc, XmlValidator.JOBSIZE_SCHEMA_URL)) {
                        //logger.error("INVALID XML:\n" + XmlUtils.DOMToString(doc));
                //}

                return doc;
        }

        private void addElementData(PrioritizedJob job, Element parent) {
		String baseUri = new Routes().getBaseUri();
                Element element= parent.getOwnerDocument().createElementNS(XmlUtils.NS_PIPELINE_DATA, "task");
                element.setAttribute("job",job.getJob().getId().toString());
                element.setAttribute("score",String.valueOf(job.getPriority()));
                element.setAttribute("job",String.valueOf(job.getJob().getPriority()));
                element.setAttribute("client",String.valueOf(job.getJob().getContext().getClient().getPriority()));
                element.setAttribute("time",String.valueOf(job.getRelativeWaitingTime()));
                
                parent.appendChild(element);

        }
}
