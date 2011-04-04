package org.daisy.pipeline.modules.converter;

import org.daisy.pipeline.modules.converter.Converter.MutableConverter;
import org.daisy.pipeline.modules.converter.Converter.MutableConverterArgument;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating Converter objects.
 */
public interface ConverterFactory {
	
	/**
	 * New converter.
	 *
	 * @return the mutable converter
	 */
	public MutableConverter newConverter();
	
	/**
	 * New argument.
	 *
	 * @return the mutable converter argument
	 */
	public MutableConverterArgument newArgument();
}
