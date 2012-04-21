package org.daisy.pipeline.job;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.daisy.common.messaging.Message;
import org.daisy.common.messaging.MessageAccessor;

// TODO: Auto-generated Javadoc
/**
 * The Class JobResult.
 */
public class JobResult {

	final URI mZipFile;
	final MessageAccessor mMessages;
	final URI mLogFile;

	private JobResult() {
	}

	public List<Message> getMessages() {
		throw new UnsupportedOperationException();
	}


	public URI getLogFile(){
		//could be null ? or throw an exception ?
		throw new UnsupportedOperationException();
	}

	public Map<String,Result> getPortResult(String name){
		throw new UnsupportedOperationException();
	}
	public Result getSinglePortResult(String name){
		throw new UnsupportedOperationException();
	}
	public Map<String,Result> getOptionResult(QName name){
		throw new UnsupportedOperationException();
	}
	public Result getSingleOptionResult(QName name){
		throw new UnsupportedOperationException();
	}

	//TODO getZip for a subdirectory /  a subset of results ?
	public Result zip(Map<String,Result>... results) {
		throw new UnsupportedOperationException();
	}

    private class Result {
    	private URI uri; // absolute in local mode, relative in remote mode
    	InputStream getAsStream(){
    		return null;
    	};
    	URI getAsURI(){
    		return null;
    	};
    	File getAsFile(){
    		return null;
    	};
    }
}
