package org.daisy.pipeline.job;

import org.daisy.common.base.Provider;

import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcOptionInfo;
import org.daisy.common.xproc.XProcPortInfo;

import javax.xml.transform.Result;
import org.daisy.pipeline.script.XProcOptionMetadata;
import org.daisy.pipeline.script.XProcScript;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

final class URITranslatorHelper   {

	public static final boolean notEmpty(String value){
		return value != null && !value.isEmpty();
	}

	
	public static final Predicate<XProcPortInfo> getNullPortFilter(final XProcInput input){
		return  new Predicate<XProcPortInfo>(){
			public boolean apply(XProcPortInfo portInfo){
				return input.getInputs(portInfo.getName()) != null;
			}
		};
	}
	/** Tranlatable options are those marked as anyFileURI or anyDirURI 
	 */
	public static final Predicate<XProcOptionInfo> getTranslatableOptionFilter(final XProcScript script){
		return  new Predicate<XProcOptionInfo>(){
			public boolean apply(XProcOptionInfo optionInfo){
				return (XProcDecorator.TranslatableOption.contains(script.getOptionMetadata(
					optionInfo.getName()).getType()));
			}
		};
	}

	public static final Predicate<XProcOptionInfo> getOutputOptionFilter(final XProcScript script){
		return  new Predicate<XProcOptionInfo>(){
			public boolean apply(XProcOptionInfo optionInfo){
				return script.getOptionMetadata(optionInfo.getName())
					.getOutput() != XProcOptionMetadata.Output.NA;
			}
		};
	}

	public static final Predicate<XProcOptionInfo> getTranslatableOutputOptionsFilter(final XProcScript script){
		return Predicates.and(URITranslatorHelper.getTranslatableOptionFilter(script),
					URITranslatorHelper.getOutputOptionFilter(script));

	}

	public static final Predicate<XProcOptionInfo> getTranslatableInputOptionsFilter(final XProcScript script){
		return Predicates.and(URITranslatorHelper.getTranslatableOptionFilter(script),
				Predicates.not(URITranslatorHelper.getOutputOptionFilter(script)));

	}
	public static final String generateOptionOutput(XProcOptionInfo option,XProcScript script){
		return IOHelper.generateOutput(
					option.getName().toString(),
					script.getOptionMetadata(option.getName())
					.getType(),
					script.getOptionMetadata(option.getName())
					.getMediaType());
	}
	/**
	 * Returns the prefix (unmmaped) at index 0 and suffix at index 1 for the a dynamic result provider based on the provider and 
	 * the port info
	 * TODO: At some point it would be nice to generate the names based on the mime-type, ask jostein where 
	 * he got the list of mime-types for the webui
	 */
	public static final String[] getDynamicResultProviderParts(String name,Provider<Result> result,String mimetype){
		String parts[]=null;
		//on the result/result.xml way
		if (result==null || result.provide().getSystemId().isEmpty()){
			parts= new String[]{String.format("%s/%s",name,name),".xml"};
		//directory-> dir/name, .xml
		//the first part is the last char of the sysId
		}else if(result.provide().getSystemId().charAt(result.provide().getSystemId().length()-1)=='/'){
			parts= new String[]{String.format("%s%s",result.provide().getSystemId(),name),".xml"};
		//file name/name, (".???"|"")
		}else{
			String ext="";
			String path=result.provide().getSystemId();
			int idx;

			//get the extension if there is one
			if((idx=path.lastIndexOf('.'))>-1)
				ext=path.substring(idx);

			// the path had a dot in the middle, t'is not an extension
			if(ext.indexOf('/')>0)
				ext="";
				
			//there's extension so we divide
			//lastIndexOf(.) will never be -1
			if(!ext.isEmpty())
				path=path.substring(0,path.lastIndexOf('.'));

			parts= new String[]{path,ext};
		}

		
		return parts;	
	}

	
}
