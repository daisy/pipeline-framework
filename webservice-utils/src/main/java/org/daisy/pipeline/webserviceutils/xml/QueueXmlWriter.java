package org.daisy.pipeline.webserviceutils.xml;

import org.daisy.pipeline.webserviceutils.Routes;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.daisy.pipeline.job.PrioritizedJob;

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
                Element element= parent.getOwnerDocument().createElementNS(XmlUtils.NS_PIPELINE_DATA, "job");
                element.setAttribute("id",job.getJob().getId().toString());
                element.setAttribute("jobHref",new Routes().getBaseUri()+"/jobs/"+job.getJob().getId().toString());
                element.setAttribute("computedPriority",String.valueOf(job.getPriority()));
                element.setAttribute("jobPriority",String.valueOf(job.getJob().getPriority()).toLowerCase());
                element.setAttribute("clientPriority",String.valueOf(job.getJob().getContext().getClient().getPriority()).toLowerCase());
                element.setAttribute("relativeTime",String.valueOf(job.getRelativeWaitingTime()));
                element.setAttribute("timestamp",String.valueOf(job.getTimestamp()));
                element.setAttribute("moveUp",new Routes().getBaseUri()+"/queue/up/"+job.getJob().getId().toString());
                element.setAttribute("moveDown",new Routes().getBaseUri()+"/queue/down/"+job.getJob().getId().toString());
                
                parent.appendChild(element);

        }
}
