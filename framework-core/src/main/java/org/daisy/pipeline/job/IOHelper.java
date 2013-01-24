package org.daisy.pipeline.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class IOHelper offers some utilities to the {@link IOBridge} class.
 */
public class IOHelper {

	/** The Constant SLASH. */
	private static final String SLASH = "/";

	/** The Constant BLOCK_SIZE. */
	private static final int BLOCK_SIZE = 1024;



	private static final Logger logger = LoggerFactory
			.getLogger(IOHelper.class);

	public static File makeDirs(String ...pathParts) throws IOException{
		StringBuilder builder = new StringBuilder();
		for (String part:pathParts){
			builder.append(part);
			builder.append(File.separator);
		}
		return IOHelper.makeDirs(new File(builder.toString()));

	}

	public static File makeDirs(File dir) throws IOException{
		if (!dir.exists() && !dir.mkdirs()) {
			throw new IOException("Could not create dir:"
					+ dir.getAbsolutePath());
		}
		return dir;
	}
	/**
	 * Dumps the content of the IS to the given path.
	 *
	 * @param is the is
	 * @param base the base
	 * @param path the path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void dump(InputStream is,URI base,URI path) throws IOException{
			//linux & mac doesnt create empty files out of outstreams where nothing was written
			//but win does, anyway this piece is more elegant than before.
			File fout = new File(base.resolve(path));
			if(!fout.toURI().toString().endsWith(SLASH)){
				fout.getParentFile().mkdirs();
				FileOutputStream fos=new FileOutputStream(fout);
				dump(is,fos);
				fos.close();
				is.close();
			}else{
				fout.mkdirs();
			}
	}


	/**
	 * Dump
	 *
	 * @param context the context
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	static void dump(ResourceCollection resources,File contextDir) throws IOException {
		for (String path : resources.getNames()) {
			IOHelper.dump(resources.getResource(path).provide(), contextDir
					.toURI(), URI.create(path.replace("\\", "/")));
		}
	}

	/**
	 * Dumps the given input stream into the output stream
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
	 * Generate output names  based on its media type.
	 *
	 * @param name the name
	 * @param type the type
	 * @param mediaType the media type
	 * @return the string
	 */
	public static String generateOutput(String name, String type, String mediaType) {
		if(type.equals(MappingURITranslator.TranslatableOption.ANY_DIR_URI.getName())){
			return name+SLASH;
		}else{
			return name+".xml";
		}

	}

	/**
	 * creates a flat list out of a tree directory.
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

	public static boolean deleteDir(File parent){
		logger.debug("Deleting directory:"+parent);
		for( File f: parent.listFiles()){
			if (f.isDirectory()){
				IOHelper.deleteDir(f);
			}
			f.delete();
		}
		return parent.delete();
	}
}
