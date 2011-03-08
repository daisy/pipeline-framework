package org.daisy.converter.parser;

import java.util.LinkedList;

import org.daisy.pipeline.modules.ResourceLoader;
import org.daisy.pipeline.modules.converter.Converter;
import org.daisy.pipeline.modules.converter.Converter.ConverterArgument;
import org.daisy.pipeline.modules.converter.Converter.ConverterArgument.Type;
import org.daisy.pipeline.modules.converter.Converter.MutableConverter;
import org.daisy.pipeline.modules.converter.Converter.MutableConverterArgument;
import org.daisy.pipeline.modules.converter.ConverterFactory;

public class DefaultConverterBuilder implements ConverterBuilder {
	private String mName;
	private String mVersion;
	private String mDescription;
	private ResourceLoader mLoader;
	private LinkedList<ConverterArgumentBuilder> mArguments = new LinkedList<ConverterBuilder.ConverterArgumentBuilder>();
	private ConverterFactory mFactory;
	


	public DefaultConverterBuilder(ConverterFactory factory) {
		super();
		this.mFactory = factory;
	}

	@Override
	public Converter build() {
		MutableConverter conv = this.mFactory.newConverter();
		conv.setDescription(mDescription);
		conv.setName(mName);
		conv.setVersion(mVersion);
		for(ConverterArgumentBuilder arg: mArguments){
			conv.addArgument(arg.build());
		}
		return conv;
	}

	@Override
	public ConverterBuilder withName(String name) {
		this.mName = name;
		return this;
	}

	@Override
	public ConverterBuilder withVersion(String version) {
		this.mVersion = version;
		return this;
	}

	@Override
	public ConverterBuilder withDescription(String desc) {
		this.mDescription = desc;
		return this;
	}

	@Override
	public ConverterBuilder withLoader(ResourceLoader loader) {
		this.mLoader = loader;
		return this;
	}

	@Override
	public ConverterBuilder withArgument(ConverterArgumentBuilder argBuilder) {
		this.mArguments.add(argBuilder);
		return this;
	}

	@Override
	public ConverterArgumentBuilder getConverterArgumentBuilder() {

		return new DefaultConverterArgumentBuilder();
	}

	public class DefaultConverterArgumentBuilder implements
			ConverterArgumentBuilder {

		private String mName;
		private String mDescription;
		private ConverterArgument.Type mType;
		private String mBind;
		private String mPort;
		private boolean mOptional;

		@Override
		public ConverterArgumentBuilder withName(String name) {
			this.mName = name;
			return this;
		}

		@Override
		public ConverterArgumentBuilder withType(String type) {
			
			if (type.equalsIgnoreCase(Type.INPUT.name())){
				mType=Type.INPUT;
			}else if(type.equalsIgnoreCase(Type.OUTPUT.name())){
				mType=Type.OUTPUT;
			}else if(type.equalsIgnoreCase(Type.OPTION.name())){
				mType=Type.OPTION;
			}else if(type.equalsIgnoreCase(Type.PARAMETER.name())){
				mType=Type.PARAMETER;
			}else{
				throw new IllegalArgumentException(type+" is not a recognised argument type");
			}
			return this;
		}

		@Override
		public ConverterArgumentBuilder withBind(String bind) {
			this.mBind = bind;
			return this;
		}

		@Override
		public ConverterArgumentBuilder withDesc(String desc) {
			this.mDescription = desc;
			return this;
		}

		@Override
		public ConverterArgumentBuilder withOptional(String optional) {
			//using Boolean.parse may end with weird results
			if(optional.equalsIgnoreCase(Boolean.TRUE.toString())){
				mOptional=true;
			}else if(optional.equalsIgnoreCase(Boolean.FALSE.toString())){
				mOptional=false;
			}else{
				throw new IllegalArgumentException(optional+": Optional must be either true or false");
			}
			return this;
		}
		@Override
		public ConverterArgumentBuilder withPort(String port) {
			this.mPort=port;
			return this;
		}
		@Override
		public ConverterArgument build() {
			MutableConverterArgument argument = mFactory.newArgument();
			argument.setName(mName);
			argument.setBind(mBind);
			argument.setPort(mPort);
			argument.setDesc(mDescription);
			argument.setType(mType);
			argument.setOptional(mOptional);
			return argument;
		}

		

	}

}
