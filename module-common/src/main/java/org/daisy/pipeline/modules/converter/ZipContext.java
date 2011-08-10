package org.daisy.pipeline.modules.converter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipContext implements XProcContext{
	private ZipFile mFile;
	HashMap<String, ZipEntry> mEntries= new HashMap<String, ZipEntry>();
	public ZipContext(ZipFile file) {
		super();
		mFile = file;
		Enumeration<? extends ZipEntry> entries=mFile.entries();
		
		while(entries.hasMoreElements()){
			ZipEntry entry=entries.nextElement();
			mEntries.put(entry.getName(), entry);
		}
		
	}

	@Override
	public Iterable<String> resources() {
		return mEntries.keySet();
	}

	@Override
	public InputStream getResource(String name) throws IOException {
		return mFile.getInputStream(mEntries.get(name));
	}
	
}
