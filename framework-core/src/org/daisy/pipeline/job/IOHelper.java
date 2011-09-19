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

// TODO: Auto-generated Javadoc
/**
 * The Class IOHelper.
 */
public class IOHelper {
	
	/** The Constant BLOCK_SIZE. */
	private static final int BLOCK_SIZE = 1024;
	
	/** The Constant DEFAULT_OUTPUT_FOLDER. */
	private static final String DEFAULT_OUTPUT_FOLDER="output";
	
	/** The Constant DEFAULT_OUTPUT_FILE. */
	private static final String DEFAULT_OUTPUT_FILE="file";
	
	/** The m folder outs. */
	private int mFolderOuts;
	
	/** The m file outs. */
	private int mFileOuts;
	
	/** The m output folder preffix. */
	private String mOutputFolderPreffix;
	
	/** The m output file preffix. */
	private String mOutputFilePreffix;
	
	/**
	 * Instantiates a new iO helper.
	 */
	public IOHelper() {
		mOutputFolderPreffix=DEFAULT_OUTPUT_FOLDER;
		mOutputFilePreffix=DEFAULT_OUTPUT_FILE;
	}
	
	/**
	 * Maps relative uris
	 *
	 * @param base the base
	 * @param uri the uri
	 * @return the uRI
	 */
	public static URI map(String base,String uri){
		String furi= base+uri;
		return URI.create(furi);
	}
	
	

	/**
	 * Gets the new output folder
	 *
	 * @param base the base
	 * @return the new output folder
	 */
	public URI getNewOutputFolder(String base){
		String fUri=base+"/"+mOutputFolderPreffix+"_"+(++mFolderOuts)+"/";
		return URI.create(fUri);
	}
	
	/**
	 * Gets the new output file.
	 *
	 * @param base the base
	 * @param suffix the suffix
	 * @return the new output file
	 */
	public URI getNewOutputFile(String base,String suffix){
		String fUri=base+"/"+mOutputFilePreffix+"_"+(++mFileOuts)+suffix;
		return URI.create(fUri);
	}
	
	/**
	 * Sets the folder output preffix.
	 *
	 * @param outputPreffix the new folder output preffix
	 */
	public void setFolderOutputPreffix(String outputPreffix) {
		mOutputFolderPreffix = outputPreffix;
	}
	
	/**
	 * Sets the file output preffix.
	 *
	 * @param outputPreffix the new file output preffix
	 */
	public void setFileOutputPreffix(String outputPreffix) {
		mOutputFilePreffix = outputPreffix;
	}
	
	/**
	 * Dumps the content of the IS to the given path
	 *
	 * @param is the is
	 * @param base the base
	 * @param path the path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void dump(InputStream is,String base,String path) throws IOException{
		File fout=new File(URI.create(base+"/"+path));
		fout.getParentFile().mkdirs();
		FileOutputStream fos=new FileOutputStream(fout);
		dump(is,fos);
		fos.close();
		is.close();
	}

	/**
	 * Dumps the IS into the OS
	 *
	 * @param is the is
	 * @param os the os
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void dump(InputStream is,OutputStream os) throws IOException{
		byte buff[]= new byte[BLOCK_SIZE];
		int read=0;
		while((read=is.read(buff))>0){
			os.write(buff,0,read);
			
		}
	}
	
	/**
	 * Generate output names  based on its media type
	 *
	 * @param name the name
	 * @param type the type
	 * @param mediaType the media type
	 * @return the string
	 */
	public static String generateOutput(String name, String type, String mediaType) {
		if(type.equals(IOBridge.ANY_DIR_URI)){
			return name+"/";
		}else{
			//TODO try to generate the extension using the media type
			return name+".xml";
		}
		
	}

	/**
	 * creates a flat list out of a tree directory
	 *
	 * @param base the base
	 * @return the list
	 */
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

	/**
	 * Creates a zip from a list of files.
	 *
	 * @param files the files
	 * @param output the output
	 * @param pathMask the path mask
	 * @return the uRI
	 * @throws ZipException the zip exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
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
