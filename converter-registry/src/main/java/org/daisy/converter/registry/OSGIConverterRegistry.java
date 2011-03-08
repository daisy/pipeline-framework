package org.daisy.converter.registry;

import java.net.URI;
import java.util.HashMap;

import org.daisy.converter.parser.ConverterParser;
import org.daisy.converter.parser.DefaultConverterBuilder;
import org.daisy.pipeline.modules.converter.Converter;
import org.daisy.pipeline.modules.converter.ConverterDescriptor;
import org.daisy.pipeline.modules.converter.ConverterDescriptor.ConverterLoader;
import org.daisy.pipeline.modules.converter.ConverterRegistry;
import org.osgi.framework.BundleContext;
public class OSGIConverterRegistry implements ConverterRegistry {
	HashMap<String, ConverterDescriptor> mDescriptors = new HashMap<String, ConverterDescriptor>();
	ConverterParser mParser = null;
	public void init(BundleContext ctxt) {
		
	}

	public void stop() {

	} 
	
	public void setParser(ConverterParser parser){
		mParser=parser;
	}

	@Override
	public void addConverterDescriptor(ConverterDescriptor conv) {
		//System.out.println("Registering:\n" + conv.toString());
		conv.setLoader(new OSGIConverterLoader());
		mDescriptors.put(conv.getName(), conv);
	}
	
	@Override
	public Iterable<ConverterDescriptor> getDescriptors() {
		return mDescriptors.values();
	}

	@Override
	public ConverterDescriptor getDescriptor(String name) {
		return mDescriptors.get(name);
	}

	public class OSGIConverterLoader implements ConverterLoader{

		@Override
		public Converter loadConverter(ConverterDescriptor desc) {
			return mParser.parse(desc,new DefaultConverterBuilder(new OSGIConverter()) );
		}
		
	}

}
