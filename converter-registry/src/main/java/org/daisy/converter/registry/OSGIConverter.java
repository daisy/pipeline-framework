package org.daisy.converter.registry;

import java.util.HashMap;

import org.daisy.pipeline.modules.converter.Converter;
import org.daisy.pipeline.modules.converter.Converter.MutableConverter;
import org.daisy.pipeline.modules.converter.ConverterFactory;
import org.daisy.pipeline.modules.converter.ConverterRunnable;

public class OSGIConverter implements Converter,MutableConverter {

	private String mName;
	private String mVersion;
	private String mDescription;
	private HashMap<String, ConverterArgument> mArguments = new HashMap<String, Converter.ConverterArgument>();
	private OSGIConverterRegistry mFactory;
	
	OSGIConverter(OSGIConverterRegistry factory) {
		mFactory=factory;
	}
	
	public void addArgument(ConverterArgument argument){
		mArguments.put(argument.getName(), argument);
	}
	@Override
	public ConverterArgument getArgument(String name) {
		return mArguments.get(name);
	}

	@Override
	public Iterable<ConverterArgument> getArguments() {
		
		return mArguments.values();
	}


	public String getName() {
		return mName;
	}

	public void setVersion(String version) {
		mVersion = version;
	}

	public String getVersion() {
		return mVersion;
	}

	public void setDescription(String description) {
		mDescription = description;
	}

	public String getDescription() {
		return mDescription;
	}
	
	@Override
	public void setName(String name) {
		mName=name;
	}
	
	OSGIConverterRegistry getRegistry(){
		return mFactory;
	}
	
	public static class OSGIConverterArgument extends  MutableConverterArgument{

		@Override
		public void setName(String name) {
			this.mName=name;
		}

		@Override
		public void setBind(String bind) {
			this.mBind=bind;
			
		}

		@Override
		public void setDesc(String desc) {
			this.mDesc=desc;
			
		}

		@Override
		public void setType(Type type) {
			this.mType=type;
			
		}

		@Override
		public void setOptional(boolean optional) {
			mOptional=optional;
			
		}

		@Override
		public void setPort(String port) {
			mPort=port;
			
		}
		
	}

	@Override
	public ConverterFactory getFactory() {
		// TODO Auto-generated method stub
		return mFactory;
	}

	@Override
	public ConverterRunnable getRunnable() {
		return new OSGIConverterRunner(this) ;
	}
	
	

}
