package org.daisy.pipeline.job;

import java.net.URI;

import org.daisy.common.xproc.XProcResult;

public class ResultSetFactory {


	public static synchronized ResultSet newResultSet(XProcResult result, JobContext ctxt,URIMapper mapper){
		//go through the outputs write them add the uri's to the 
		//result object
		//Results ports=ResultFactory.writeOutputs(
		//go through the output options and add them, this is a bit more tricky 
		//as you have to check if the files exist
		//if your working with an anyURIDir then scan the directory to 
		//get all the files inside.
		return null;
	}

	
}
