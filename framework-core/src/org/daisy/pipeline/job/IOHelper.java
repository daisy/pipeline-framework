package org.daisy.pipeline.job;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class IOHelper {
	private static final int BLOCK_SIZE = 1024;
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
		String furi= base+uri;
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
	public static void dump(InputStream is,String base,String path) throws IOException{
		File fout=new File(URI.create(base+"/"+path));
		fout.getParentFile().mkdirs();
		FileOutputStream fos=new FileOutputStream(fout);
		
		byte buff[]= new byte[BLOCK_SIZE];
		int read=0;
		while((read=is.read(buff))>0){
			fos.write(buff,0,read);
			
		}
		fos.close();
		is.close();
	}
}
