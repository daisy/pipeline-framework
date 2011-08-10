package org.daisy.pipeline.job;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.daisy.commons.xproc.io.Resource;
import org.daisy.commons.xproc.io.ResourceCollection;
import org.xml.sax.InputSource;


public class ZipResourceContext implements ResourceCollection {
	private ZipFile mFile;
	HashMap<String, ZipEntryResource> mEntries= new HashMap<String, ZipEntryResource>();
	public ZipResourceContext(ZipFile file) {
		mFile = file;
		Enumeration<? extends ZipEntry> entries=mFile.entries();
		
		while(entries.hasMoreElements()){
			ZipEntry entry=entries.nextElement();
			mEntries.put(entry.getName(), new ZipEntryResource(entry));
		}
	}

	@Override
	public Iterable<Resource> getResources() {
		return new LinkedList<Resource>(mEntries.values());
	}

	@Override
	public Resource getResource(String path) {
		return mEntries.get(path);
	}
	

	@Override
	public Iterable<String> getPaths() {
		return mEntries.keySet();
	}
	
	public class ZipEntryResource extends Resource{
		private ZipEntry mEntry;
		private File mFileWrapper=null;
		public ZipEntryResource(ZipEntry entry) {
			mEntry = entry;
		}
		@Override
		public File asFile() throws IOException {
//			if (mFileWrapper==null){
//				mFileWrapper=File.createTempFile("dp2", "");
//				FileOutputStream wr= new FileOutputStream(mFileWrapper);
//				InputStream is = this.asInputStream();
//				byte buff[]=new byte[1024];
//				int read=0;
//				while((read=is.read(buff))>0){
//					wr.write(buff, 0, read);
//				}
//				is.close();
//				wr.close();
//			}
//			return mFileWrapper;
			return null;
		}
		@Override
		public InputStream asInputStream() throws IOException {
			return mFile.getInputStream(mEntry);
		}
		@Override
		public Source asSource() throws IOException {
			SAXSource src= new SAXSource();
			InputSource iSrc= new InputSource();
			iSrc.setByteStream(mFile.getInputStream(mEntry));
			src.setInputSource(iSrc);
			return src;
		
		}
		@Override
		public URI asURI() throws IOException {
			return null;
			//return this.asFile().toURI();
		}
		
		
	}

	
}
