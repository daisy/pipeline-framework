package org.daisy.converter.registry;

import java.util.HashMap;

import org.daisy.pipeline.modules.converter.Converter;
import org.daisy.pipeline.modules.converter.Converter.MutableConverter;
import org.daisy.pipeline.modules.converter.ConverterFactory;
import org.daisy.pipeline.modules.converter.ConverterRunnable;

// TODO: Auto-generated Javadoc
/**
 * The Class OSGIConverter defines a converter used in a OSGI environment
 */
public class OSGIConverter implements Converter,MutableConverter {

	/** The name. */
	private String mName;
	
	/** The version. */
	private String mVersion;
	
	/** The description. */
	private String mDescription;
	
	/** The arguments. */
	private HashMap<String, ConverterArgument> mArguments = new HashMap<String, Converter.ConverterArgument>();
	
	/** The factory. */
	private OSGIConverterRegistry mFactory;
	
	/**
	 * Instantiates a new oSGI converter using the given OSGIConverterFactory services
	 *
	 * @param factory the factory
	 */
	OSGIConverter(OSGIConverterRegistry factory) {
		mFactory=factory;
	}
	
	/* (non-Javadoc)
	 * @see org.daisy.pipeline.modules.converter.Converter.MutableConverter#addArgument(org.daisy.pipeline.modules.converter.Converter.ConverterArgument)
	 */
	public void addArgument(ConverterArgument argument){
		mArguments.put(argument.getName(), argument);
	}
	
	/* (non-Javadoc)
	 * @see org.daisy.pipeline.modules.converter.Converter#getArgument(java.lang.String)
	 */
	@Override
	public ConverterArgument getArgument(String name) {
		return mArguments.get(name);
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.modules.converter.Converter#getArguments()
	 */
	@Override
	public Iterable<ConverterArgument> getArguments() {
		
		return mArguments.values();
	}


	/* (non-Javadoc)
	 * @see org.daisy.pipeline.modules.converter.Converter#getName()
	 */
	public String getName() {
		return mName;
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.modules.converter.Converter.MutableConverter#setVersion(java.lang.String)
	 */
	public void setVersion(String version) {
		mVersion = version;
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.modules.converter.Converter#getVersion()
	 */
	public String getVersion() {
		return mVersion;
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.modules.converter.Converter.MutableConverter#setDescription(java.lang.String)
	 */
	public void setDescription(String description) {
		mDescription = description;
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.modules.converter.Converter#getDescription()
	 */
	public String getDescription() {
		return mDescription;
	}
	
	/* (non-Javadoc)
	 * @see org.daisy.pipeline.modules.converter.Converter.MutableConverter#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		mName=name;
	}
	
	/**
	 * Gets the registry.
	 *
	 * @return the registry
	 */
	OSGIConverterRegistry getRegistry(){
		return mFactory;
	}
	
	/**
	 * The Class OSGIConverterArgument.
	 */
	public static class OSGIConverterArgument extends  MutableConverterArgument{

		/* (non-Javadoc)
		 * @see org.daisy.pipeline.modules.converter.Converter.MutableConverterArgument#setName(java.lang.String)
		 */
		@Override
		public void setName(String name) {
			this.mName=name;
		}

		/* (non-Javadoc)
		 * @see org.daisy.pipeline.modules.converter.Converter.MutableConverterArgument#setBind(java.lang.String)
		 */
		@Override
		public void setBind(String bind) {
			this.mBind=bind;
			
		}

		/* (non-Javadoc)
		 * @see org.daisy.pipeline.modules.converter.Converter.MutableConverterArgument#setDesc(java.lang.String)
		 */
		@Override
		public void setDesc(String desc) {
			this.mDesc=desc;
			
		}

		/* (non-Javadoc)
		 * @see org.daisy.pipeline.modules.converter.Converter.MutableConverterArgument#setType(org.daisy.pipeline.modules.converter.Converter.ConverterArgument.Type)
		 */
		@Override
		public void setType(Type type) {
			this.mType=type;
			
		}

		/* (non-Javadoc)
		 * @see org.daisy.pipeline.modules.converter.Converter.MutableConverterArgument#setOptional(boolean)
		 */
		@Override
		public void setOptional(boolean optional) {
			mOptional=optional;
			
		}

		/* (non-Javadoc)
		 * @see org.daisy.pipeline.modules.converter.Converter.MutableConverterArgument#setPort(java.lang.String)
		 */
		@Override
		public void setPort(String port) {
			mPort=port;
			
		}
		
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.modules.converter.Converter#getFactory()
	 */
	@Override
	public ConverterFactory getFactory() {
		// TODO Auto-generated method stub
		return mFactory;
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.modules.converter.Converter#getRunnable()
	 */
	@Override
	public ConverterRunnable getRunnable() {
		return new OSGIConverterRunner(this) ;
	}
	
	

}
