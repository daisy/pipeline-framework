package org.daisy.pipeline.modules.converter;

import java.net.URI;

public abstract class ConverterDescriptor {
	protected String mName;
	protected URI mFile;
	protected String mDescription;
	protected Converter mConverter;
	protected ConverterLoader mLoader;
	
	public ConverterDescriptor() {
		super();
	}
	
	public ConverterDescriptor(String name, URI file, String description,
			ConverterLoader loader) {
		super();
		mName = name;
		mFile = file;
		mDescription = description;
		mLoader = loader;
	}
	public String getName() {
		return mName;
	}
	public void setName(String name) {
		mName = name;
	}
	public URI getFile() {
		return mFile;
	}
	public void setFile(URI file) {
		mFile = file;
	}
	public String getDescription() {
		return mDescription;
	}
	public void setDescription(String description) {
		mDescription = description;
	}
	
	public Converter getConverter() {
		if (this.mConverter==null)
			mConverter = this.mLoader.loadConverter(this);
		return mConverter;
	}
	
	public void setLoader(ConverterLoader loader) {
		mLoader = loader;
	}
	public ConverterLoader getLoader() {
		return mLoader;
	}

	/** Converter loader interface **/
	public interface ConverterLoader{
		public Converter loadConverter(ConverterDescriptor desc);
	}

}
