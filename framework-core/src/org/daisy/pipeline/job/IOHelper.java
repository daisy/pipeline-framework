package org.daisy.pipeline.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

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
		dump(is,fos);
		fos.close();
		is.close();
	}

	public static void dump(InputStream is,OutputStream os) throws IOException{
		byte buff[]= new byte[BLOCK_SIZE];
		int read=0;
		while((read=is.read(buff))>0){
			os.write(buff,0,read);
			
		}
	}
	
	public static String generateOutput(String name, String type, String mediaType) {
		if(type.equals(IOBridge.ANY_DIR_URI)){
			return name+"/";
		}else{
			//TODO try to generate the extension using the media type
			return name+".xml";
		}
		
	}

	public static List<File> treeFileList(File base) {
		LinkedList<File> result= new LinkedList<File>();
		for(File f:base.listFiles()){
			if(f.isDirectory()){
				result.addAll(treeFileList(f));
			}else{
				result.add(f);
			}
		}
		return result;
	}

	public static URI zipFromEntries(List<File> files, File output,String pathMask) throws ZipException, IOException {
		ZipOutputStream zipOs = new ZipOutputStream(new FileOutputStream(output));
		
		for(File f:files){
			ZipEntry entry= new ZipEntry(f.toString().replace(pathMask, ""));
			zipOs.putNextEntry(entry);
			InputStream is=new FileInputStream(f);
			dump(is,zipOs);
			is.close();
			
		}
		zipOs.close();
		return output.toURI();
	}
}
