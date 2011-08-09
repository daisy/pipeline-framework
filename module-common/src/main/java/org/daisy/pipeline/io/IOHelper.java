package org.daisy.pipeline.io;

import java.net.URI;

public class IOHelper {
	private static final String DEFAULT_OUTPUT_FOLDER="output";
	private static final String DEFAULT_OUTPUT_FILE="file";
	private int mFolderOuts;
	private int mFileOuts;
	private String mOutputFolderPreffix;
	private String mOutputFilePreffix;
	public IOHelper() {
		mOutputFolderPreffix=DEFAULT_OUTPUT_FOLDER;
		mOutputFilePreffix=DEFAULT_OUTPUT_FILE;
	}
	
	public static URI map(String base,String uri){
		String furi= base+"/"+uri;
		return URI.create(furi);
	}

	public URI getNewOutputFolder(String base){
		String fUri=base+"/"+mOutputFolderPreffix+"_"+(++mFolderOuts)+"/";
		return URI.create(fUri);
	}
	public URI getNewOutputFile(String base,String suffix){
		String fUri=base+"/"+mOutputFilePreffix+"_"+(++mFileOuts)+suffix;
		return URI.create(fUri);
	}
	public void setFolderOutputPreffix(String outputPreffix) {
		mOutputFolderPreffix = outputPreffix;
	}
	public void setFileOutputPreffix(String outputPreffix) {
		mOutputFilePreffix = outputPreffix;
	}
}
