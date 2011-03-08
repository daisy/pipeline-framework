package org.daisy.pipeline.modules.converter;

import org.daisy.pipeline.modules.converter.Converter.MutableConverter;
import org.daisy.pipeline.modules.converter.Converter.MutableConverterArgument;

public interface ConverterFactory {
	public MutableConverter newConverter();
	public MutableConverterArgument newArgument();
}
