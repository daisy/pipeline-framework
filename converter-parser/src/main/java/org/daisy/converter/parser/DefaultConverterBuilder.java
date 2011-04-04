package org.daisy.converter.parser;

import java.util.LinkedList;

import org.daisy.pipeline.modules.ResourceLoader;
import org.daisy.pipeline.modules.converter.Converter;
import org.daisy.pipeline.modules.converter.Converter.ConverterArgument;
import org.daisy.pipeline.modules.converter.Converter.ConverterArgument.Type;
import org.daisy.pipeline.modules.converter.Converter.MutableConverter;
import org.daisy.pipeline.modules.converter.Converter.MutableConverterArgument;
import org.daisy.pipeline.modules.converter.ConverterFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class DefaultConverterBuilder is the default implementation of the converter builder interface
 */
public class DefaultConverterBuilder implements ConverterBuilder {
	
	/** The m name. */
	private String mName;
	
	/** The m version. */
	private String mVersion;
	
	/** The m description. */
	private String mDescription;
	
	/** The m loader. */
	private ResourceLoader mLoader;
	
	/** The m arguments. */
	private LinkedList<ConverterArgumentBuilder> mArguments = new LinkedList<ConverterBuilder.ConverterArgumentBuilder>();
	
	/** The m factory. */
	private ConverterFactory mFactory;
	


	/**
	 * Instantiates a new default converter builder.
	 *
	 * @param factory the factory
	 */
	public DefaultConverterBuilder(ConverterFactory factory) {
		super();
		this.mFactory = factory;
	}

	/* (non-Javadoc)
	 * @see org.daisy.converter.parser.ConverterBuilder#build()
	 */
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

	/* (non-Javadoc)
	 * @see org.daisy.converter.parser.ConverterBuilder#withName(java.lang.String)
	 */
	@Override
	public ConverterBuilder withName(String name) {
		this.mName = name;
		return this;
	}

	/* (non-Javadoc)
	 * @see org.daisy.converter.parser.ConverterBuilder#withVersion(java.lang.String)
	 */
	@Override
	public ConverterBuilder withVersion(String version) {
		this.mVersion = version;
		return this;
	}

	/* (non-Javadoc)
	 * @see org.daisy.converter.parser.ConverterBuilder#withDescription(java.lang.String)
	 */
	@Override
	public ConverterBuilder withDescription(String desc) {
		this.mDescription = desc;
		return this;
	}

	/* (non-Javadoc)
	 * @see org.daisy.converter.parser.ConverterBuilder#withLoader(org.daisy.pipeline.modules.ResourceLoader)
	 */
	@Override
	public ConverterBuilder withLoader(ResourceLoader loader) {
		this.mLoader = loader;
		return this;
	}

	/* (non-Javadoc)
	 * @see org.daisy.converter.parser.ConverterBuilder#withArgument(org.daisy.converter.parser.ConverterBuilder.ConverterArgumentBuilder)
	 */
	@Override
	public ConverterBuilder withArgument(ConverterArgumentBuilder argBuilder) {
		this.mArguments.add(argBuilder);
		return this;
	}

	/* (non-Javadoc)
	 * @see org.daisy.converter.parser.ConverterBuilder#getConverterArgumentBuilder()
	 */
	@Override
	public ConverterArgumentBuilder getConverterArgumentBuilder() {

		return new DefaultConverterArgumentBuilder();
	}

	/**
	 * The Class DefaultConverterArgumentBuilder.
	 */
	public class DefaultConverterArgumentBuilder implements
			ConverterArgumentBuilder {

		/** The m name. */
		private String mName;
		
		/** The m description. */
		private String mDescription;
		
		/** The m type. */
		private ConverterArgument.Type mType;
		
		/** The m bind. */
		private String mBind;
		
		/** The m port. */
		private String mPort;
		
		/** The m optional. */
		private boolean mOptional;

		/* (non-Javadoc)
		 * @see org.daisy.converter.parser.ConverterBuilder.ConverterArgumentBuilder#withName(java.lang.String)
		 */
		@Override
		public ConverterArgumentBuilder withName(String name) {
			this.mName = name;
			return this;
		}

		/* (non-Javadoc)
		 * @see org.daisy.converter.parser.ConverterBuilder.ConverterArgumentBuilder#withType(java.lang.String)
		 */
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

		/* (non-Javadoc)
		 * @see org.daisy.converter.parser.ConverterBuilder.ConverterArgumentBuilder#withBind(java.lang.String)
		 */
		@Override
		public ConverterArgumentBuilder withBind(String bind) {
			this.mBind = bind;
			return this;
		}

		/* (non-Javadoc)
		 * @see org.daisy.converter.parser.ConverterBuilder.ConverterArgumentBuilder#withDesc(java.lang.String)
		 */
		@Override
		public ConverterArgumentBuilder withDesc(String desc) {
			this.mDescription = desc;
			return this;
		}

		/* (non-Javadoc)
		 * @see org.daisy.converter.parser.ConverterBuilder.ConverterArgumentBuilder#withOptional(java.lang.String)
		 */
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
		
		/* (non-Javadoc)
		 * @see org.daisy.converter.parser.ConverterBuilder.ConverterArgumentBuilder#withPort(java.lang.String)
		 */
		@Override
		public ConverterArgumentBuilder withPort(String port) {
			this.mPort=port;
			return this;
		}
		
		/* (non-Javadoc)
		 * @see org.daisy.converter.parser.ConverterBuilder.ConverterArgumentBuilder#build()
		 */
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
