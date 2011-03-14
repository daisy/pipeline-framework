package org.daisy.pipeline.modules.converter;

import org.daisy.pipeline.modules.converter.Converter.ConverterArgument.Type;

/** inmutable **/
public interface Converter {
	public String getName();

	public String getVersion();

	public String getDescription();

	public ConverterArgument getArgument(String name);

	public Iterable<ConverterArgument> getArguments();

	public ConverterFactory getFactory();

	public ConverterRunnable getRunnable();

	public abstract class ConverterArgument {
		public enum Type {
			INPUT, OUTPUT, OPTION, PARAMETER;

		};

		protected String mName;
		protected Type mType;
		protected String mPort;
		protected String mBind;
		protected String mDesc;
		protected boolean mOptional;

		public ConverterArgument(){
			
		}
		public ConverterArgument(String name, Type type, String port,
				String bind, String desc, boolean optional) {
			super();
			mName = name;
			mType = type;
			mPort = port;
			mBind = bind;
			mDesc = desc;
			mOptional = optional;
		}

		public String getName() {
			return mName;
		}

		public Type getType() {
			return mType;
		}

		public String getPort() {
			return mPort;
		}

		public void setPort(String port) {
			mPort = port;
		}

		public String getBind() {
			return mBind;
		}

		public String getDesc() {
			return mDesc;
		}

		public boolean isOptional() {
			return mOptional;
		}

	}

	/** mutable interface **/
	public interface MutableConverter extends Converter{
		public void setName(String name);

		public void setVersion(String version);

		public void setDescription(String description);

		public void addArgument(ConverterArgument argument);
	}

	/** mutable interface **/
	public abstract class  MutableConverterArgument extends ConverterArgument{
		public abstract void setName(String name);

		public abstract void setPort(String port);

		public abstract void setBind(String bind);

		public abstract void setDesc(String desc);

		public abstract void setType(Type type);

		public abstract void setOptional(boolean optional);
	}
}
