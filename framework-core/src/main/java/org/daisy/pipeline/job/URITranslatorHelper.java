package org.daisy.pipeline.job;

import org.daisy.common.xproc.XProcOptionInfo;

import org.daisy.pipeline.script.XProcOptionMetadata;
import org.daisy.pipeline.script.XProcScript;

final class URITranslatorHelper   {

	public static final boolean notEmpty(String value){
		return value != null && !value.isEmpty();
	}

	public static final boolean isOutput(XProcScript script,XProcOptionInfo info){
		return script.getOptionMetadata(info.getName())
					.getOutput() != XProcOptionMetadata.Output.NA;
	}


}
