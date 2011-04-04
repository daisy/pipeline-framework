package org.daisy.pipeline.modules.converter;

import java.net.URI;

/**
 * The converter descriptor just keeps the name of the converter and where it can be loaded for a
 * late loading
 */
public abstract class ConverterDescriptor {
	
	/** The  name. */
	protected String mName;
	
	/** The file. */
	protected URI mFile;
	
	/** The description. */
	protected String mDescription;
	
	/** The converter associated to this descriptor */
	protected Converter mConverter;
	
	/** The loader. */
	protected ConverterLoader mLoader;
	
	/**
	 * Instantiates a new converter descriptor.
	 */
	public ConverterDescriptor() {
		super();
	}
	
	/**
	 * Instantiates a new converter descriptor.
	 *
	 * @param name the name
	 * @param file the file
	 * @param description the description
	 * @param loader the loader
	 */
	public ConverterDescriptor(String name, URI file, String description,
			ConverterLoader loader) {
		super();
		mName = name;
		mFile = file;
		mDescription = description;
		mLoader = loader;
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return mName;
	}
	
	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		mName = name;
	}
	
	/**
	 * Gets the file.
	 *
	 * @return the file
	 */
	public URI getFile() {
		return mFile;
	}
	
	/**
	 * Sets the file.
	 *
	 * @param file the new file
	 */
	public void setFile(URI file) {
		mFile = file;
	}
	
	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return mDescription;
	}
	
	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		mDescription = description;
	}
	
	/**
	 * Gets the converter.
	 *
	 * @return the converter
	 */
	public Converter getConverter() {
		if (this.mConverter==null)
			mConverter = this.mLoader.loadConverter(this);
		return mConverter;
	}
	
	/**
	 * Sets the loader.
	 *
	 * @param loader the new loader
	 */
	public void setLoader(ConverterLoader loader) {
		mLoader = loader;
	}
	
	/**
	 * Gets the loader.
	 *
	 * @return the loader
	 */
	public ConverterLoader getLoader() {
		return mLoader;
	}

	/**
	 * Converter loader interface *.
	 */
	public interface ConverterLoader{
		
		/**
		 * Load converter.
		 *
		 * @param desc the desc
		 * @return the converter
		 */
		public Converter loadConverter(ConverterDescriptor desc);
	}

}
