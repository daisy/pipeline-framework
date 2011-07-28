package org.daisy.pipeline.modules.converter;


public abstract class MutableConverterArgument extends ConverterArgument {

	public abstract void setName(String name);

	public abstract void setBindType(BindType bindType);

	public abstract void setBind(String bind);

	public abstract void setDesc(String desc);

	public abstract void setOptional(boolean optional);

	public abstract void setDirection(Direction direction);

	public abstract void setMediaType(String mediaType);

	public abstract void setOutputType(OutputType outputType);

	public abstract void setSequence(boolean sequence);

}