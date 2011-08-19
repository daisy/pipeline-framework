package org.daisy.converter.parser;



import java.net.URI;

import org.daisy.pipeline.modules.ResourceLoader;
import org.daisy.pipeline.modules.converter.Converter;
import org.daisy.pipeline.modules.converter.ConverterArgument;


/**
 * The Interface ConverterBuilder builds a converter once all the data is filled
 */
public interface ConverterBuilder {

		/**
		 * Builds the converter
		 *
		 * @return the converter
		 */
		public Converter build();

		/**
		 * With name.
		 *
		 * @param name the name
		 * @return this converter builder
		 */
		public ConverterBuilder withName(String name);

		/**
		 * With version.
		 *
		 * @param version the version
		 * @return this converter builder
		 */
		public ConverterBuilder withVersion(String version);
		
		/**
		 * With description.
		 *
		 * @param desc the desc
		 * @return this converter builder
		 */
		public ConverterBuilder withDescription(String desc);
		
		public ConverterBuilder withURI(URI uri);
		/**
		 * With loader.
		 *
		 * @param loader the loader
		 * @return this converter builder
		 */
		public ConverterBuilder withLoader(ResourceLoader loader);

		/**
		 * With argument.
		 *
		 * @param argBuilder the arg builder
		 * @return this converter builder
		 */
		public ConverterBuilder withArgument(ConverterArgumentBuilder argBuilder);

		/**
		 * Gets the converter argument builder.
		 *
		 * @return the converter argument builder
		 */
		public ConverterArgumentBuilder getConverterArgumentBuilder();
		
		/**
		 * The Interface ConverterArgumentBuilder builds an argument once the data is filled
		 */
		public interface ConverterArgumentBuilder {
			
			/**
			 * Builds the converter argument
			 *
			 * @return this converter argument
			 */
			public ConverterArgument build();
			
			/**
			 * With name.
			 *
			 * @param name the name
			 * @return this converter argument builder
			 */
			public ConverterArgumentBuilder withName(String name);
			
			/**
			 * With type.
			 *
			 * @param type the type
			 * @return this converter argument builder
			 */
			public ConverterArgumentBuilder withType(String type);
			
			/**
			 * With bind.
			 *
			 * @param bind the bind
			 * @return this converter argument builder
			 */
			public ConverterArgumentBuilder withBind(String bind);
			
			/**
			 * With port.
			 *
			 * @param port the port
			 * @return this converter argument builder
			 */
			public ConverterArgumentBuilder withPort(String port);
			
			/**
			 * With desc.
			 *
			 * @param desc the desc
			 * @return this converter argument builder
			 */
			public ConverterArgumentBuilder withDesc(String desc);
			
			/**
			 * With optional.
			 *
			 * @param desc the desc
			 * @return this converter argument builder
			 */
			public ConverterArgumentBuilder withOptional(String desc);
			
			public ConverterArgumentBuilder withBindType(String bindType);
			public ConverterArgumentBuilder withDir(String dir);
			public ConverterArgumentBuilder withMediaType(String mediaType);
			public ConverterArgumentBuilder withSequence(String sequence);
			
		}
	
}
