package org.daisy.converter.registry;

import java.net.URI;
import java.util.HashMap;

import javax.xml.transform.URIResolver;

import org.daisy.converter.registry.OSGIConverter.OSGIConverterArgument;
import org.daisy.pipeline.modules.UriResolverDecorator;
import org.daisy.pipeline.modules.converter.Converter;
import org.daisy.pipeline.modules.converter.Converter.MutableConverter;
import org.daisy.pipeline.modules.converter.ConverterDescriptor;
import org.daisy.pipeline.modules.converter.ConverterDescriptor.ConverterLoader;
import org.daisy.pipeline.modules.converter.ConverterFactory;
import org.daisy.pipeline.modules.converter.ConverterRegistry;
import org.daisy.pipeline.modules.converter.MutableConverterArgument;
import org.daisy.pipeline.xproc.XProcessorFactory;
import org.osgi.framework.BundleContext;
/**
 * The Class OSGIConverterRegistry resgisters all the converters loaded using DS.
 */
public class OSGIConverterRegistry implements ConverterRegistry,ConverterFactory {
	
	/** The available descriptors */
	HashMap<String, ConverterDescriptor> mDescriptors = new HashMap<String, ConverterDescriptor>();
	/** available descriptiors indexed by uri **/
	HashMap<URI, ConverterDescriptor> mDescriptorsUri = new HashMap<URI, ConverterDescriptor>();
	
	/** The parser. */
	//ConverterParser mParser = null;
	
	/** The xproc factory. */
	XProcessorFactory mXprocFactory = null;
	
	/** The uri resolver. */
	private URIResolver mUriResolver = null;
	
	/**
	 * Inits the.
	 *
	 * @param ctxt the ctxt
	 */
	public void init(BundleContext ctxt) {
		
	}

	/**
	 * Stop.
	 */
	public void stop() {

	} 
	
	/**
	 * Sets the parser which will parse the the converter definition inside the xpl file
	 *
	 * @param parser the new parser
	 */
	//public void setParser(ConverterParser parser){
	//	mParser=parser;
	//}

	/**
	 * Sets the xproc factory that will be used to run the converter runnables
	 *
	 * @param xprocFactory the new xproc factory
	 */
	public void setXprocFactory(XProcessorFactory xprocFactory){
		mXprocFactory=xprocFactory;
	}
	
	/**
	 * Gets the xproc factory.
	 *
	 * @return the xproc factory
	 */
	XProcessorFactory getXprocFactory(){
		return mXprocFactory;
	}
	
	/**
	 * Sets the uri resolver.
	 *
	 * @param uriResolver the new uri resolver
	 */
	public void setUriResolver(UriResolverDecorator uriResolver) {
		
		mUriResolver = uriResolver;
	}

	/**
	 * Gets the uri resolver.
	 *
	 * @return the uri resolver
	 */
	URIResolver getUriResolver() {
		return mUriResolver;
	}
	
	/* (non-Javadoc)
	 * @see org.daisy.pipeline.modules.converter.ConverterRegistry#addConverterDescriptor(org.daisy.pipeline.modules.converter.ConverterDescriptor)
	 */
	@Override
	public void addConverterDescriptor(ConverterDescriptor conv) {
		//System.out.println("Registering:\n" + conv.toString());
		//conv.setLoader(new OSGIConverterLoader());
		mDescriptors.put(conv.getName(), conv);
		mDescriptorsUri.put(conv.getFile(), conv);
	}
	
	/* (non-Javadoc)
	 * @see org.daisy.pipeline.modules.converter.ConverterRegistry#getDescriptors()
	 */
	@Override
	public Iterable<ConverterDescriptor> getDescriptors() {
		return mDescriptors.values();
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.modules.converter.ConverterRegistry#getDescriptor(java.lang.String)
	 */
	@Override
	public ConverterDescriptor getDescriptor(String name) {
		return mDescriptors.get(name);
	}
	@Override
	public ConverterDescriptor getDescriptor(URI uri) {
		return mDescriptorsUri.get(uri);
	}
	/**
	 * The Class OSGIConverterLoader.
	 */
	
//	public class OSGIConverterLoader implements ConverterLoader{
//
//		/* (non-Javadoc)
//		 * @see org.daisy.pipeline.modules.converter.ConverterDescriptor.ConverterLoader#loadConverter(org.daisy.pipeline.modules.converter.ConverterDescriptor)
//		 */
//		@Override
//		//public Converter loadConverter(ConverterDescriptor desc) {
//			//return mParser.parse(desc,new DefaultConverterBuilder(OSGIConverterRegistry.this) );
//		//}
//		
//	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.modules.converter.ConverterFactory#newConverter()
	 */
	@Override
	public MutableConverter newConverter() {
		
		return new OSGIConverter(this);
	}
	
	/* (non-Javadoc)
	 * @see org.daisy.pipeline.modules.converter.ConverterFactory#newArgument()
	 */
	@Override
	public MutableConverterArgument newArgument() {
		// TODO Auto-generated method stub
		return new OSGIConverterArgument();
	}



	
}
