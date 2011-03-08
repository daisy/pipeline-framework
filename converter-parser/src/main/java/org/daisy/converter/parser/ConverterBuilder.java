package org.daisy.converter.parser;



import org.daisy.pipeline.modules.converter.Converter;
import org.daisy.pipeline.modules.converter.Converter.ConverterArgument;
import org.daisy.pipeline.modules.ResourceLoader;



public interface ConverterBuilder {

		public Converter build();

		public ConverterBuilder withName(String name);

		public ConverterBuilder withVersion(String version);
		
		public ConverterBuilder withDescription(String desc);
		
		public ConverterBuilder withLoader(ResourceLoader loader);

		public ConverterBuilder withArgument(ConverterArgumentBuilder argBuilder);

		public ConverterArgumentBuilder getConverterArgumentBuilder();
		
		public interface ConverterArgumentBuilder {
			public ConverterArgument build();
			public ConverterArgumentBuilder withName(String name);
			public ConverterArgumentBuilder withType(String type);
			public ConverterArgumentBuilder withBind(String bind);
			public ConverterArgumentBuilder withPort(String port);
			public ConverterArgumentBuilder withDesc(String desc);
			public ConverterArgumentBuilder withOptional(String desc);
		}
	
}
