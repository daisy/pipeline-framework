package org.daisy.pipeline.job;

import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcOptionInfo;
import org.daisy.common.xproc.XProcPortInfo;

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
				return (MappingURITranslator.TranslatableOption.contains(script.getOptionMetadata(
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
	
}
