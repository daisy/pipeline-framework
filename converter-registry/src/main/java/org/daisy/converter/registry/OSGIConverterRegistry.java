package org.daisy.converter.registry;

import java.util.HashMap;

import javax.xml.transform.URIResolver;

import org.daisy.converter.parser.ConverterParser;
import org.daisy.converter.parser.DefaultConverterBuilder;
import org.daisy.converter.registry.OSGIConverter.OSGIConverterArgument;
import org.daisy.pipeline.modules.UriResolverDecorator;
import org.daisy.pipeline.modules.converter.Converter;
import org.daisy.pipeline.modules.converter.Converter.MutableConverter;
import org.daisy.pipeline.modules.converter.Converter.MutableConverterArgument;
import org.daisy.pipeline.modules.converter.ConverterDescriptor;
import org.daisy.pipeline.modules.converter.ConverterDescriptor.ConverterLoader;
import org.daisy.pipeline.modules.converter.ConverterFactory;
import org.daisy.pipeline.modules.converter.ConverterRegistry;
import org.daisy.pipeline.xproc.XProcessorFactory;
import org.osgi.framework.BundleContext;
public class OSGIConverterRegistry implements ConverterRegistry,ConverterFactory {
	HashMap<String, ConverterDescriptor> mDescriptors = new HashMap<String, ConverterDescriptor>();
	ConverterParser mParser = null;
	XProcessorFactory mXprocFactory = null;
	private URIResolver mUriResolver = null;
	public void init(BundleContext ctxt) {
		
	}

	public void stop() {

	} 
	
	public void setParser(ConverterParser parser){
		mParser=parser;
	}

	public void setXprocFactory(XProcessorFactory xprocFactory){
		mXprocFactory=xprocFactory;
	}
	
	XProcessorFactory getXprocFactory(){
		return mXprocFactory;
	}
	public void setUriResolver(UriResolverDecorator uriResolver) {
		mUriResolver = uriResolver;
	}

	URIResolver getUriResolver() {
		return mUriResolver;
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
			return mParser.parse(desc,new DefaultConverterBuilder(OSGIConverterRegistry.this) );
		}
		
	}

	@Override
	public MutableConverter newConverter() {
		
		return new OSGIConverter(this);
	}
	@Override
	public MutableConverterArgument newArgument() {
		// TODO Auto-generated method stub
		return new OSGIConverterArgument();
	}

	
}
