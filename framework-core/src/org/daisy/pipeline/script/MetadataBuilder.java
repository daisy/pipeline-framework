package org.daisy.pipeline.script;

public interface MetadataBuilder <T>{
	public MetadataBuilder withNiceName(String name);
	public MetadataBuilder withDescription(String desc);
	public T build();
}
