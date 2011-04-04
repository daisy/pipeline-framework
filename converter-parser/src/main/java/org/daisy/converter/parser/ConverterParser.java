package org.daisy.converter.parser;

import org.daisy.pipeline.modules.converter.Converter;
import org.daisy.pipeline.modules.converter.ConverterDescriptor;

// TODO: Auto-generated Javadoc
/**
 * The Interface ConverterParser.
 */
public interface ConverterParser {
	
	/**
	 * Parses the file defined inside the converter descriptor and builds a new converter using the 
	 * provided builder
	 *
	 * @param descriptor the descriptor
	 * @param builder the builder
	 * @return the converter
	 */
	Converter parse(ConverterDescriptor descriptor, ConverterBuilder builder);
}
