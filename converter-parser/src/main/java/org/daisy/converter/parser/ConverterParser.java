package org.daisy.converter.parser;

import org.daisy.pipeline.modules.converter.Converter;
import org.daisy.pipeline.modules.converter.ConverterDescriptor;

public interface ConverterParser {
	Converter parse(ConverterDescriptor descriptor, ConverterBuilder builder);
}
