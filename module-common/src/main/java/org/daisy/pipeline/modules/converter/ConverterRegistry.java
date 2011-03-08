package org.daisy.pipeline.modules.converter;


public interface ConverterRegistry {

	public void addConverterDescriptor(ConverterDescriptor conv);
	public Iterable<ConverterDescriptor> getDescriptors();
	public ConverterDescriptor getDescriptor(String name);
	
}
