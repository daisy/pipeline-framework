package org.daisy.pipeline.job;

import java.io.File;
import java.io.IOException;

import java.net.URI;

import org.daisy.pipeline.job.URIMapper;

public class JobURIUtils   {
	/** The Constant ORG_DAISY_PIPELINE_IOBASE. */
	final static String ORG_DAISY_PIPELINE_IOBASE = "org.daisy.pipeline.iobase";

	final static String IO_DATA_SUBDIR = "context";
	/** The I o_ outpu t_ subdir. */
	final static String IO_OUTPUT_SUBDIR = "output";
	/**
	 * Returns an idle uri mapping, in case we are expecting absolute uris 
	 * all the time
	 */
	public static URIMapper newURIMapper(){
		return new URIMapper(URI.create(""),URI.create(""));
	}


	/**
	 * Returns a URI mapper which builds a directory extructure 
	 * based on the jobid
	 */
	public static URIMapper newURIMapper(JobId id) throws IOException{
		//Base based on the the id
		File baseDir = getJobBaseFile(id); 
		File contextDir = IOHelper.makeDirs(new File(baseDir, IO_DATA_SUBDIR));
		File outputDir = IOHelper.makeDirs(new File(baseDir, IO_OUTPUT_SUBDIR));
		return new URIMapper(contextDir.toURI(),outputDir.toURI());

	}

	public static URI getLogFile(JobId id) {
		//this has to be done according to the logback configuration file
		
		File logFile;
		try {
			logFile = new File(getJobBaseFile(id),String.format("%s.log",id.toString()));
			logFile.createNewFile();
			return logFile.toURI();
		} catch (IOException e) {
			throw new RuntimeException(String.format("Error creating the log file for %s",id.toString()),e);
		}
	}
	
	private static File getJobBaseFile(JobId id) throws IOException{
		return IOHelper.makeDirs(new File(new File(frameworkBase()), id.toString()));
	}
	public static URI getJobBase(JobId id) throws IOException{
		return getJobBaseFile(id).toURI();
	}
	private static String frameworkBase(){
		if (System.getProperty(ORG_DAISY_PIPELINE_IOBASE) == null) {
			throw new IllegalStateException(String.format("The property %s is not set",ORG_DAISY_PIPELINE_IOBASE ));
		}
		return System.getProperty(ORG_DAISY_PIPELINE_IOBASE);
	}
}
