package org.daisy.pipeline.job;

import java.io.File;
import java.io.IOException;

import java.net.URI;

import org.daisy.pipeline.job.URIMapper;

public class URIMapperFactory   {
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
		if (System.getProperty(ORG_DAISY_PIPELINE_IOBASE) == null) {
			throw new IllegalStateException("The property "
					+ ORG_DAISY_PIPELINE_IOBASE + " is not set");
		}
		//Base based on the the id
		File ioBase=IOHelper.makeDirs(System.getProperty(ORG_DAISY_PIPELINE_IOBASE));
		File baseDir = IOHelper.makeDirs(new File(ioBase, id.toString()));

		File contextDir = IOHelper.makeDirs(new File(baseDir, IO_DATA_SUBDIR));
		File outputDir = IOHelper.makeDirs(new File(baseDir, IO_OUTPUT_SUBDIR));
		return new URIMapper(contextDir.toURI(),outputDir.toURI());

	}
}
