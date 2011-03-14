package org.daisy.converter.registry;

import java.net.URI;
import java.util.Dictionary;

import org.daisy.pipeline.modules.converter.ConverterDescriptor;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

public class OSGIConverterDescriptor extends ConverterDescriptor {

	private static final String CONVERTER_URL = "converter.url";
	private static final String CONVERTER_DESCRIPTION = "converter.description";
	private static final String CONVERTER_NAME = "converter.name";
	private BundleContext mCtxt;
	public OSGIConverterDescriptor() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	public void init(BundleContext ctxt) {
		
	} 

	protected void activate(ComponentContext context) {
		Dictionary props =context.getProperties();
		mCtxt=context.getBundleContext();
		if (props.get(CONVERTER_NAME)==null ||props.get(CONVERTER_NAME).toString().isEmpty()){
			throw new IllegalArgumentException(CONVERTER_NAME+" property must not be empty");
		}
		
		if (props.get(CONVERTER_DESCRIPTION)==null ||props.get(CONVERTER_DESCRIPTION).toString().isEmpty()){
			throw new IllegalArgumentException(CONVERTER_DESCRIPTION+" property must not be empty");
		}
		if (props.get(CONVERTER_URL)==null ||props.get(CONVERTER_URL).toString().isEmpty()){
			throw new IllegalArgumentException(CONVERTER_URL+" property must not be empty");
		}
		
		mName = props.get(CONVERTER_NAME).toString();
		mDescription = props.get(CONVERTER_DESCRIPTION).toString();
		mFile = URI.create(props.get(CONVERTER_URL).toString());
	
	}
	
	public String toString(){
		StringBuffer buf=new StringBuffer();
		buf.append("Name:\t"+mName);
		buf.append("\nDesc:\t"+mDescription);
		buf.append("\nurl:\t"+mFile.toString());
		return buf.toString();
	}





}
