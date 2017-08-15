package org.daisy.pipeline.job.impl;

import java.io.File;
import java.net.URI;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.daisy.pipeline.job.JobResult;
import org.daisy.pipeline.job.JobResultSet;
import org.w3c.dom.Document;

public class JobUtils {
        /**
         * Checks the validation status from the status port and get it's value
         */
        private final static String STATUS_PORT="status";
        public static boolean checkStatusPort(JobResultSet results){
                Collection<JobResult> resCollection=results.getResults(STATUS_PORT);
                //check if the status port exists otherwise return true
                if (resCollection.size()==0){
                        return true;
                }
                boolean ok=true;
                //check all the files in the port
                for(JobResult res: resCollection){

                        ok &=JobUtils.processStatus(res.getPath());
                }
                return ok;

        }

        /**Reads the xml file pointed by path to check that validation status is equal to ok.
         * if it's not it returns false
         */
        public static boolean processStatus(URI path){

                //check the contents of the xml and check if result is ok
                //<d:status xmlns:d="http://www.daisy.org/ns/pipeline/data" result="error"/>
                try{
                        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                        docBuilderFactory.setNamespaceAware(true);
                        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                        
                        Document doc = docBuilder.parse (new File(path));
                        String status = doc.getDocumentElement().getAttribute("result");
                        if (status==null || status.isEmpty()){
                                throw new RuntimeException("No result attribute was found in the status port");
                        }
                        return status.equalsIgnoreCase("ok");

                }catch (Exception e){
                        throw new RuntimeException("Error processing status file",e);
                }
        }
}
