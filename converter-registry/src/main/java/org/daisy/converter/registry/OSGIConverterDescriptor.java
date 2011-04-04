package org.daisy.converter.registry;

import java.net.URI;
import java.util.Dictionary;

import org.daisy.pipeline.modules.converter.ConverterDescriptor;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

/**
 * The Class OSGIConverterDescriptor to be used in a OSGI environment. This class defines a common the interface 
 * to all the converter defined in DS xml files.
 * This xml file should look like:
 * <scr:component xmlns:scr="http://www.osgi.org/xmlns/scr/v1.1.0" immediate="true" name="test2">
   <implementation class="org.daisy.converter.registry.OSGIConverterDescriptor"/>
   <service>
            <provide interface="org.daisy.pipeline.modules.converter.ConverterDescriptor"/>
   </service>
   <property name="converter.name" type="String" value="testHello"/>
   <property name="converter.description" type="String" value="Converter Hello"/>
   <property name="converter.url" type="String" value="http://www.example.org/test/helloTest.xpl"/>

</scr:component>
 * 
 * Be sure of including this file in your module or the converter will NOT be loaded into the framework
 * 
 */
public class OSGIConverterDescriptor extends ConverterDescriptor {

	/** The Constant CONVERTER_URL. */
	private static final String CONVERTER_URL = "converter.url";
	
	/** The Constant CONVERTER_DESCRIPTION. */
	private static final String CONVERTER_DESCRIPTION = "converter.description";
	
	/** The Constant CONVERTER_NAME. */
	private static final String CONVERTER_NAME = "converter.name";
	
	/** The bundle ctxt. */
	private BundleContext mCtxt;
	
	/**
	 * Instantiates a new oSGI converter descriptor.
	 */
	public OSGIConverterDescriptor() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Inits the registry, only log proposes
	 *
	 * @param ctxt the ctxt
	 */
	public void init(BundleContext ctxt) {
		
	} 

	/**
	 * DS will call this method when activating the module
	 *
	 * @param context the context
	 */
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
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		StringBuffer buf=new StringBuffer();
		buf.append("Name:\t"+mName);
		buf.append("\nDesc:\t"+mDescription);
		buf.append("\nurl:\t"+mFile.toString());
		return buf.toString();
	}





}
