package org.daisy.pipeline.job;

import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcOptionInfo;
import org.daisy.common.xproc.XProcPortInfo;

import org.daisy.pipeline.script.XProcOptionMetadata;
import org.daisy.pipeline.script.XProcScript;

import com.google.common.base.Predicate;

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

	public static final Predicate<XProcOptionInfo> getTranslatableOptionFilter(final XProcScript script){
		return  new Predicate<XProcOptionInfo>(){
			public boolean apply(XProcOptionInfo optionInfo){
				return (RemoteURITranslator.TranslatableOption.contains(script.getOptionMetadata(
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

	public static final String generateOptionOutput(XProcOptionInfo option,XProcScript script){
		return IOHelper.generateOutput(
					option.getName().toString(),
					script.getOptionMetadata(option.getName())
					.getType(),
					script.getOptionMetadata(option.getName())
					.getMediaType());
	}
}
